package com.cropdoc.app.data.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.cropdoc.app.data.agent.CropDocToolSet
import com.google.ai.edge.litertlm.*
import androidx.datastore.preferences.core.edit
import com.cropdoc.app.LANGUAGE_KEY
import com.cropdoc.app.dataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

// Only used for message construction, not engine management
private enum class MessageModality { TEXT, VISION, AUDIO }

class CropDocAiEngine(private val context: Context) {

    companion object {
        private const val TAG = "CropDocAI"
        private const val MODEL_ASSET = "gemma-4-E2B-it.litertlm"
        private const val USE_DEMO_MODE = false
        private const val MAX_HISTORY_MESSAGES = 8
    }

    private val _modelState = MutableStateFlow<ModelState>(ModelState.NotLoaded)
    val modelState: StateFlow<ModelState> = _modelState.asStateFlow()

    // Single engine instance — initialized once with all backends enabled.
    // No modality switching, no rebuilding.
    private var engine: Engine? = null
    private val engineMutex = Mutex()

    private var persistentConversation: AutoCloseable? = null
    private var persistentConversationKey: String? = null

    private var currentLanguage = "en"

    suspend fun initialize() = withContext(Dispatchers.IO) {
        try {
            _modelState.value = ModelState.Loading

            // Restore saved language — survives activity recreation
            val savedLanguage = context.dataStore.data
                .map { prefs -> prefs[LANGUAGE_KEY] ?: "en" }
                .first()
            currentLanguage = savedLanguage
            Log.d(TAG, "Language restored: $currentLanguage")

            if (USE_DEMO_MODE) {
                kotlinx.coroutines.delay(2_000)
                _modelState.value = ModelState.Ready
                return@withContext
            }

            val modelFile = File(context.filesDir, MODEL_ASSET)
            if (!modelFile.exists()) {
                _modelState.value = ModelState.Error("Model not found.")
                return@withContext
            }

            engine = buildEngine(modelFile.absolutePath).also { it.initialize() }
            _modelState.value = ModelState.Ready
        } catch (e: Exception) {
            Log.e(TAG, "Initialize failed", e)
            _modelState.value = ModelState.Error(e.message ?: "Unknown error")
        }
    }

    // Single engine built once with all backends enabled.
    // Vision and audio modalities no longer require tearing down and rebuilding
    // the engine — they just send a different Message type.
    private fun buildEngine(modelPath: String): Engine {
        val config = EngineConfig(
            modelPath = modelPath,
            backend = Backend.GPU(),
            visionBackend = Backend.CPU(),
            audioBackend = Backend.CPU(),
            maxNumTokens = 4096,
            cacheDir = context.cacheDir.absolutePath
        )
        return Engine(config)
    }

    // Suspend + acquires the mutex before closing.
    // This prevents FAILED_PRECONDITION caused by closing a conversation that is
    // mid-generation (e.g. zone-switching while the model is still responding).
    // The caller must be inside a coroutine — ChatViewModel already launches one.
    suspend fun resetConversation() = engineMutex.withLock {
        closePersistentConversation()
        Log.d(TAG, "Conversation reset — KV cache cleared")
    }

    // Private non-suspend close — called from within the mutex in all engine methods.
    private fun closePersistentConversation() {
        try {
            persistentConversation?.close()
        } catch (_: Exception) {}
        persistentConversation = null
        persistentConversationKey = null
    }

    private fun conversationKey(): String = "lang=$currentLanguage:tools=enabled:v4"

    suspend fun analyseCrop(
        imageBitmap: Bitmap,
        soilReading: SoilReading?,
        onToken: (String) -> Unit
    ): Result<AnalysisResult> = withContext(Dispatchers.IO) {
        try {
            if (USE_DEMO_MODE) {
                return@withContext Result.success(
                    streamDemoResponse(soilReading, withImage = true, onToken)
                )
            }

            val imageFile = File(context.cacheDir, "crop_analysis.jpg")
            FileOutputStream(imageFile).use { out ->
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                out.flush()
            }

            val prompt = buildCropPrompt(soilReading)
            var fullResponse = ""

            engineMutex.withLock {
                val eng = engine ?: throw IllegalStateException("Engine unavailable")

                // Close any open persistent TEXT conversation before creating a fresh one.
                // LiteRT only allows one active session per engine at a time.
                closePersistentConversation()

                val cfg = ConversationConfig(systemInstruction = Contents.of(SYSTEM_PROMPT))

                eng.createConversation(cfg).use { conv ->
                    conv.sendMessageAsync(
                        Message.of(
                            Content.ImageFile(imageFile.absolutePath),
                            Content.Text(prompt)
                        )
                    ).collect { token ->
                        val text = token.toString()
                        fullResponse += text
                        onToken(text)
                    }
                }
            }

            Result.success(parseJsonResponse(fullResponse, soilReading))
        } catch (e: Exception) {
            Log.e(TAG, "Analysis failed", e)
            Result.failure(e)
        }
    }

    suspend fun analyseSoilOnly(
        soilReading: SoilReading,
        onToken: (String) -> Unit
    ): Result<AnalysisResult> = withContext(Dispatchers.IO) {
        try {
            if (USE_DEMO_MODE) {
                return@withContext Result.success(
                    streamDemoResponse(soilReading, withImage = false, onToken)
                )
            }

            var fullResponse = ""

            engineMutex.withLock {
                val eng = engine ?: throw IllegalStateException("Engine unavailable")

                // Close any open persistent conversation before creating a fresh one.
                closePersistentConversation()

                val cfg = ConversationConfig(systemInstruction = Contents.of(SYSTEM_PROMPT))

                eng.createConversation(cfg).use { conv ->
                    conv.sendMessageAsync(buildSoilOnlyPrompt(soilReading)).collect { token ->
                        val text = token.toString()
                        fullResponse += text
                        onToken(text)
                    }
                }
            }

            Result.success(parseJsonResponse(fullResponse, soilReading))
        } catch (e: Exception) {
            Log.e(TAG, "Soil analysis failed", e)
            Result.failure(e)
        }
    }

    suspend fun summariseSoil(soilReading: SoilReading): String = withContext(Dispatchers.IO) {
        if (USE_DEMO_MODE) return@withContext buildDemoSoilSummary(soilReading)

        return@withContext try {
            val prompt = """
                A farmer is looking at their soil sensor readings:
                - Moisture: ${soilReading.moisture.fmt(0)}%
                - pH: ${soilReading.ph.fmt(1)}
                - Nitrogen: ${soilReading.nitrogen.fmt(0)} mg/kg
                - Phosphorus: ${soilReading.phosphorus.fmt(0)} mg/kg
                - Potassium: ${soilReading.potassium.fmt(0)} mg/kg
                - Temperature: ${soilReading.temperature.fmt(1)}°C

                In 2-3 plain sentences, explain what these numbers mean for their crops.
                Use simple language a farmer would understand. No technical jargon.
            """.trimIndent()

            var response = ""

            engineMutex.withLock {
                val eng = engine ?: throw IllegalStateException("Engine unavailable")

                // Close any open persistent conversation before creating a fresh one.
                closePersistentConversation()

                val cfg = ConversationConfig(
                    systemInstruction = Contents.of(
                        "You are a helpful farming assistant. Explain soil data in simple, plain language."
                    )
                )

                eng.createConversation(cfg).use { conv ->
                    conv.sendMessageAsync(prompt).collect { token ->
                        response += token.toString()
                    }
                }
            }

            response
        } catch (e: Exception) {
            Log.e(TAG, "Soil summary failed", e)
            "Could not generate summary. ${e.message}"
        }
    }

    suspend fun chat(
        userMessage: String,
        imageUri: Uri?,
        audioFile: File? = null,
        soilReading: SoilReading?,
        weatherData: WeatherData?,
        zone: FarmZone?,
        crop: Crop? = null,
        allZonesWithCrops: List<Pair<FarmZone, Crop?>> = emptyList(),
        history: List<ChatMessage>,
        context: Context,
        onToken: (String) -> Unit
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (USE_DEMO_MODE) {
                return@withContext Result.success(
                    buildDemoChatResponse(userMessage, soilReading, weatherData, zone).also { response ->
                        response.split(" ").forEachIndexed { i, word ->
                            onToken(if (i == 0) word else " $word")
                            kotlinx.coroutines.delay(25)
                        }
                    }
                )
            }

            val modality = when {
                imageUri != null -> MessageModality.VISION
                audioFile != null -> MessageModality.AUDIO
                else -> MessageModality.TEXT
            }

            val systemPrompt = buildStableChatSystemPrompt()
            val farmContext = buildFarmContextMessage(
                soilReading = soilReading,
                weatherData = weatherData,
                zone = zone,
                crop = crop,
                allZonesWithCrops = allZonesWithCrops
            )
            val recentContext = buildRecentConversationContext(history)

            var fullResponse = ""

            engineMutex.withLock {
                val eng = engine ?: throw IllegalStateException("Engine unavailable")
                val convKey = conversationKey()

                // TEXT: inject history only when creating a fresh persistent conversation.
                // VISION/AUDIO: always inject — fresh conversation every time.
                val shouldInjectHistory = when (modality) {
                    MessageModality.TEXT ->
                        persistentConversation == null || persistentConversationKey != convKey
                    MessageModality.VISION, MessageModality.AUDIO -> true
                }

                val textPayload = buildChatPayload(
                    userMessage = userMessage,
                    farmContext = farmContext,
                    recentContext = recentContext,
                    includeHistory = shouldInjectHistory
                )

                val finalMessage = when (modality) {
                    MessageModality.VISION -> {
                        val imgFile = resolveImageFile(imageUri!!, context)
                        Log.d(
                            TAG,
                            "VISION INPUT -> path=${imgFile.absolutePath}, exists=${imgFile.exists()}, size=${imgFile.length()}"
                        )
                        validateImageFile(imgFile)
                        Message.of(Content.ImageFile(imgFile.absolutePath), Content.Text(textPayload))
                    }

                    MessageModality.AUDIO -> {
                        val file = audioFile ?: throw IllegalStateException("Audio file is null")
                        Log.d(
                            TAG,
                            "AUDIO INPUT -> path=${file.absolutePath}, exists=${file.exists()}, size=${file.length()}, ext=${file.extension}"
                        )
                        validateAudioFile(file)
                        Message.of(Content.AudioFile(file.absolutePath), Content.Text(textPayload))
                    }

                    MessageModality.TEXT -> Message.of(Content.Text(textPayload))
                }

                if (modality == MessageModality.TEXT) {
                    // TEXT turns reuse the persistent conversation so the KV cache grows across turns.
                    // History context is preserved through the cache — no re-injection needed per turn.
                    if (persistentConversation == null || persistentConversationKey != convKey) {
                        Log.d(TAG, "Creating persistent TEXT conversation (historyBootstrap=$shouldInjectHistory)")
                        closePersistentConversation()
                        val cfg = ConversationConfig(
                            systemInstruction = Contents.of(systemPrompt),
                            tools = listOf(tool(CropDocToolSet(context))),
                            automaticToolCalling = true
                        )
                        persistentConversation = eng.createConversation(cfg)
                        persistentConversationKey = convKey
                    }

                    @Suppress("UNCHECKED_CAST")
                    val conv = persistentConversation as? Conversation ?: run {
                        Log.w(TAG, "Conversation cast failed — recreating persistent TEXT conversation")
                        closePersistentConversation()
                        val cfg = ConversationConfig(
                            systemInstruction = Contents.of(systemPrompt),
                            tools = listOf(tool(CropDocToolSet(context))),
                            automaticToolCalling = true
                        )
                        val fresh = eng.createConversation(cfg)
                        persistentConversation = fresh
                        persistentConversationKey = convKey
                        fresh
                    }

                    Log.d(TAG, "Sending TEXT message using persistent KV cache")

                    try {
                        conv.sendMessageAsync(finalMessage).collect { token ->
                            val text = token.toString()
                            fullResponse += text
                            onToken(text)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Text send failed; resetting and retrying once: ${e.message}", e)
                        closePersistentConversation()

                        val retryPayload = buildChatPayload(
                            userMessage = userMessage,
                            farmContext = farmContext,
                            recentContext = recentContext,
                            includeHistory = true
                        )
                        val cfg = ConversationConfig(
                            systemInstruction = Contents.of(systemPrompt),
                            tools = listOf(tool(CropDocToolSet(context))),
                            automaticToolCalling = true
                        )
                        val fresh = eng.createConversation(cfg)
                        persistentConversation = fresh
                        persistentConversationKey = convKey

                        fresh.sendMessageAsync(Message.of(Content.Text(retryPayload))).collect { token ->
                            val text = token.toString()
                            fullResponse += text
                            onToken(text)
                        }
                    }
                } else {
                    // VISION/AUDIO — fresh conversation each time.
                    // Close persistent TEXT conversation BEFORE creating fresh one —
                    // LiteRT only allows one active session per engine at a time.
                    Log.d(TAG, "Closing persistent conversation before $modality turn")
                    closePersistentConversation()

                    val cfg = ConversationConfig(
                        systemInstruction = Contents.of(systemPrompt)
                    )

                    Log.d(TAG, "Sending $modality via fresh conversation")
                    eng.createConversation(cfg).use { conv ->
                        conv.sendMessageAsync(finalMessage).collect { token ->
                            val text = token.toString()
                            fullResponse += text
                            onToken(text)
                        }
                    }

                    closePersistentConversation()
                }
            }

            Result.success(fullResponse)
        } catch (e: Exception) {
            Log.e(TAG, "Chat failed — imageUri=$imageUri, audioFile=$audioFile", e)
            Result.failure(e)
        }
    }

    private fun buildChatPayload(
        userMessage: String,
        farmContext: String,
        recentContext: String,
        includeHistory: Boolean
    ): String = buildString {
        if (farmContext.isNotBlank()) {
            appendLine(farmContext)
            appendLine()
        }

        if (includeHistory && recentContext.isNotBlank()) {
            appendLine(recentContext)
            appendLine()
        }

        // Language instruction placed immediately before the farmer message so it
        // is the last thing the model reads before generating — maximises compliance
        // on small models that struggle with distant system prompt instructions.
        appendLine(languageInstruction())
        appendLine()
        appendLine("Farmer message:")
        append(userMessage)
    }.also { payload ->
        Log.d(TAG, "=== CHAT PAYLOAD ===\n$payload\n=== END PAYLOAD ===")
    }

    private fun validateImageFile(file: File) {
        if (!file.exists()) throw IllegalStateException("Image file does not exist: ${file.absolutePath}")
        if (file.length() <= 0L) throw IllegalStateException("Image file is empty: ${file.absolutePath}")

        val decoded = BitmapFactory.decodeFile(file.absolutePath)
            ?: throw IllegalStateException("Image file could not be decoded: ${file.absolutePath}")

        Log.d(TAG, "VISION INPUT OK: width=${decoded.width}, height=${decoded.height}")
    }

    private fun validateAudioFile(file: File) {
        if (!file.exists()) throw IllegalStateException("Audio file does not exist: ${file.absolutePath}")
        if (file.length() <= 0L) throw IllegalStateException("Audio file is empty: ${file.absolutePath}")

        val extension = file.extension.lowercase()
        if (extension !in setOf("wav", "mp3", "m4a", "aac", "flac", "ogg", "3gp")) {
            Log.w(TAG, "Audio file has uncommon extension: .$extension")
        }

        Log.d(TAG, "AUDIO INPUT OK: size=${file.length()}, ext=$extension")
    }

    private fun resolveImageFile(imageUri: Uri, context: Context): File {
        return when {
            imageUri.scheme == "file" -> File(imageUri.path ?: "")
            else -> {
                val f = File(context.filesDir, "engine_image_${System.currentTimeMillis()}.jpg")
                context.contentResolver.openInputStream(imageUri)?.use { input ->
                    FileOutputStream(f).use { output ->
                        input.copyTo(output)
                        output.flush()
                    }
                } ?: throw IllegalStateException("Could not open image input stream: $imageUri")
                f
            }
        }
    }

    suspend fun runAgentCheck(
        prompt: String,
        onAlert: (String) -> Unit
    ) = withContext(Dispatchers.IO) {
        if (USE_DEMO_MODE) {
            listOf(
                "Soil moisture is critically low at 22% — irrigate Zone A today",
                "High humidity (78%) with warm temperatures — monitor for fungal disease on maize",
                "Nitrogen levels below optimal — consider applying urea before next watering"
            ).forEach { onAlert(it) }
            return@withContext
        }

        try {
            var fullResponse = ""

            engineMutex.withLock {
                val eng = engine ?: throw IllegalStateException("Engine unavailable")

                // Close any open persistent conversation before creating a fresh one.
                closePersistentConversation()

                val cfg = ConversationConfig(
                    systemInstruction = Contents.of(
                        "You are a farm monitoring agent. Respond only with a JSON array of alert strings."
                    )
                )

                eng.createConversation(cfg).use { conv ->
                    conv.sendMessageAsync(prompt).collect { token ->
                        fullResponse += token.toString()
                    }
                }
            }

            val cleaned = fullResponse.replace("```json", "").replace("```", "").trim()
            Log.d(TAG, "Agent raw response: $cleaned")
            val jsonArray = JSONArray(cleaned)
            for (i in 0 until jsonArray.length()) {
                onAlert(jsonArray.getString(i))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Agent check failed", e)
        }
    }

    private fun buildStableChatSystemPrompt(): String = buildString {
        appendLine(languageInstruction())
        appendLine()
        appendLine(
            """
            You are Farm Assistant, a friendly and knowledgeable farming assistant helping smallholder farmers in Africa.
            You have deep expertise in agronomy, crop diseases, soil health, weather-aware farming, and sustainable farming practices.
            Give practical, actionable advice using simple language. Be conversational and warm — you are talking to a farmer in the field.

            The farmer may send text, crop/soil images, or voice messages. If an image is provided, first describe what you visually observe, then give advice.
            If a voice message is provided, respond naturally as if in a conversation.

            Tools are available for explicit actions only: setting reminders, creating calendar events, saving notes, or sending SMS messages.
            Use tools only when the farmer clearly asks you to perform one of those actions.
            Do not repeat tool calls you have already made in this conversation.

            When a tool returns a message containing "please tap Save" or "please tap Send", tell the farmer exactly that.
            Do not claim the action is complete when Android still needs the farmer to confirm it.
            For reminders and calendar events that save automatically, you may confirm they are done.
            """.trimIndent()
        )
    }

    private fun buildFarmContextMessage(
        soilReading: SoilReading?,
        weatherData: WeatherData?,
        zone: FarmZone?,
        crop: Crop? = null,
        allZonesWithCrops: List<Pair<FarmZone, Crop?>> = emptyList()
    ): String = buildString {
        appendLine("Current farm context. Use this as fresh state for this turn; do not treat it as old chat history.")

        when {
            zone != null && crop != null -> {
                val daysPlanted =
                    ((System.currentTimeMillis() - crop.plantedDate) / (1000 * 60 * 60 * 24)).toInt()
                val daysToHarvest = (crop.expectedHarvestDays - daysPlanted).coerceAtLeast(0)

                appendLine("- Active zone: ${zone.name}")
                appendLine("- Active crop: ${crop.name}")
                appendLine("- Crop age: $daysPlanted days")
                appendLine("- Expected harvest: in $daysToHarvest days (${crop.expectedHarvestDays} days total from planting)")

                if (crop.notes.isNotBlank()) appendLine("- Farmer notes: ${crop.notes}")
            }

            zone != null -> {
                appendLine("- Active zone: ${zone.name}")
                appendLine("- Active crop: none recorded yet")
            }

            allZonesWithCrops.isNotEmpty() -> {
                appendLine("- Farm zones:")
                allZonesWithCrops.forEachIndexed { index, (z, c) ->
                    if (c != null) {
                        val daysPlanted =
                            ((System.currentTimeMillis() - c.plantedDate) / (1000 * 60 * 60 * 24)).toInt()
                        val daysToHarvest =
                            (c.expectedHarvestDays - daysPlanted).coerceAtLeast(0)
                        appendLine("  ${index + 1}. ${z.name}: ${c.name}, planted $daysPlanted days ago, harvest in $daysToHarvest days")
                    } else {
                        appendLine("  ${index + 1}. ${z.name}: no crop planted yet")
                    }
                }
            }

            else -> appendLine("- No active farm zone selected")
        }

        soilReading?.let {
            appendLine("- Soil readings:")
            appendLine("  Moisture: ${it.moisture.fmt(0)}%")
            appendLine("  pH: ${it.ph.fmt(1)}")
            appendLine("  Nitrogen: ${it.nitrogen.fmt(0)} mg/kg")
            appendLine("  Phosphorus: ${it.phosphorus.fmt(0)} mg/kg")
            appendLine("  Potassium: ${it.potassium.fmt(0)} mg/kg")
            appendLine("  Soil temperature: ${it.temperature.fmt(1)}°C")
        }

        weatherData?.let {
            appendLine("- Weather at ${it.location}:")
            appendLine("  Temperature: ${it.temperature}°C")
            appendLine("  Humidity: ${it.humidity}%")
            appendLine("  Expected rainfall: ${it.rainfall}mm")
            appendLine("  Wind speed: ${it.windSpeed} km/h")
            appendLine("  Forecast: ${it.forecast}")
        }
    }.trim()

    // Image and audio turns are represented as labeled text in history so the model has
    // full conversational awareness without storing image embeddings in the KV cache.
    private fun buildRecentConversationContext(
        history: List<ChatMessage>,
        maxMessages: Int = MAX_HISTORY_MESSAGES
    ): String {
        val recent = history
            .takeLast(maxMessages)
            .filter { it.content.isNotBlank() }
            .joinToString("\n") { message ->
                val role = when (message.role.uppercase()) {
                    "USER" -> "Farmer"
                    "ASSISTANT", "AI", "MODEL" -> "Farm Assistant"
                    else -> message.role
                }

                val content = when {
                    message.role.uppercase() == "USER" && message.attachedImageUri != null -> {
                        val text = message.content
                            .takeIf { it != "📷 Image" && it.isNotBlank() }
                        if (text != null) "[sent a crop image] $text" else "[sent a crop image]"
                    }

                    message.role.uppercase() == "USER" && message.audioPath != null -> {
                        val text = message.content
                            .takeIf { it != "🎤 Voice message" && it.isNotBlank() }
                        if (text != null) "[sent a voice message] $text" else "[sent a voice message]"
                    }

                    else -> message.content
                }

                "$role: $content"
            }

        return if (recent.isBlank()) "" else "Recent conversation context:\n$recent"
    }

    private fun buildDemoChatResponse(
        userMessage: String,
        soilReading: SoilReading?,
        weatherData: WeatherData?,
        zone: FarmZone?
    ): String {
        val lower = userMessage.lowercase()

        return when {
            lower.contains("water") || lower.contains("irrigat") || lower.contains("moisture") -> {
                val moisture = soilReading?.moisture
                if (moisture != null && moisture < 30f) {
                    "Your soil moisture is critically low at ${moisture.fmt(0)}%. Irrigate immediately — aim for at least 25mm of water."
                } else if (moisture != null) {
                    "Your soil moisture is at ${moisture.fmt(0)}%, which is ${
                        if (moisture in 30f..70f) "within the healthy range." else "slightly high. Ensure good drainage."
                    }"
                } else {
                    "Most crops need irrigation when the top 5cm of soil feels dry to the touch."
                }
            }

            lower.contains("fertiliz") || lower.contains("nitrogen") || lower.contains("npk") -> {
                val n = soilReading?.nitrogen
                if (n != null && n < 25f) {
                    "Your nitrogen level is low at ${n.fmt(0)} mg/kg. Apply urea (46-0-0) at 50 kg per hectare."
                } else {
                    "Your nitrogen levels look adequate. Maintain organic matter by incorporating crop residues."
                }
            }

            lower.contains("disease") || lower.contains("pest") || lower.contains("sick") -> {
                "For the most accurate diagnosis, take a photo of the affected area and include it in your message."
            }

            else -> buildString {
                append("I'm here to help with your farm! ")
                soilReading?.let {
                    if (it.moisture < 30f) append("Your soil needs water urgently. ")
                    if (it.ph < 5.5f) append("Your pH is too acidic. ")
                    if (it.nitrogen < 25f) append("Nitrogen is low. ")
                }
                append("Ask me about watering, fertilizers, crop diseases, or harvest timing.")
            }
        }
    }

    private val SYSTEM_PROMPT = """
        You are Farm Assistant, an expert agronomist AI assistant helping smallholder farmers.
        You MUST respond ONLY with valid JSON. No explanation, no markdown, no extra text.
        Just the raw JSON object.
    """.trimIndent()

    private fun buildCropPrompt(soilReading: SoilReading?): String {
        val soilSection = soilReading?.let {
            """
            Soil readings:
            - Moisture: ${it.moisture.fmt(0)}%
            - pH: ${it.ph.fmt(1)}
            - Nitrogen: ${it.nitrogen.fmt(0)} mg/kg
            - Phosphorus: ${it.phosphorus.fmt(0)} mg/kg
            - Potassium: ${it.potassium.fmt(0)} mg/kg
            - Temperature: ${it.temperature.fmt(1)}°C
            """.trimIndent()
        } ?: "No soil data available."

        return """
            ${languageInstruction()}

            Analyse this crop image and respond ONLY with this exact JSON structure:

            {"healthScore":75,"diseases":[{"name":"Disease name","severity":"LOW","affectedArea":"Leaves","description":"Brief description"}],"immediateActions":["Action 1"],"soilRecommendations":[{"parameter":"Nitrogen","currentValue":"18 mg/kg","targetRange":"40-80 mg/kg","action":"Apply urea at 50kg/ha","priority":"HIGH"}],"summary":"Plain English explanation"}

            severity and priority must be one of: LOW, MEDIUM, HIGH, CRITICAL.
            healthScore must be 0-100. If no diseases found, return empty array.

            $soilSection
        """.trimIndent()
    }

    private fun buildSoilOnlyPrompt(soilReading: SoilReading): String = """
        ${languageInstruction()}

        Analyse these soil readings and respond ONLY with this exact JSON structure:

        {"healthScore":55,"diseases":[],"immediateActions":["Action 1"],"soilRecommendations":[{"parameter":"Nitrogen","currentValue":"18 mg/kg","targetRange":"40-80 mg/kg","action":"Apply urea at 50kg/ha","priority":"HIGH"}],"summary":"Plain English explanation"}

        Soil readings:
        - Moisture: ${soilReading.moisture.fmt(0)}%
        - pH: ${soilReading.ph.fmt(1)}
        - Nitrogen: ${soilReading.nitrogen.fmt(0)} mg/kg
        - Phosphorus: ${soilReading.phosphorus.fmt(0)} mg/kg
        - Potassium: ${soilReading.potassium.fmt(0)} mg/kg
        - Temperature: ${soilReading.temperature.fmt(1)}°C
    """.trimIndent()

    private fun parseJsonResponse(response: String, soilReading: SoilReading?): AnalysisResult {
        return try {
            val cleaned = response.replace("```json", "").replace("```", "").trim()
            val json = JSONObject(cleaned)

            val healthScore = json.optInt("healthScore", estimateHealthScore(soilReading))

            val diseases = mutableListOf<DiseaseDetection>()
            val diseasesArray = json.optJSONArray("diseases") ?: JSONArray()
            for (i in 0 until diseasesArray.length()) {
                val d = diseasesArray.getJSONObject(i)
                diseases.add(
                    DiseaseDetection(
                        name = d.optString("name", "Unknown"),
                        confidence = 0.9f,
                        description = d.optString("description", ""),
                        severity = parseSeverity(d.optString("severity", "MEDIUM")),
                        affectedArea = d.optString("affectedArea", "Unknown")
                    )
                )
            }

            val actions = mutableListOf<String>()
            val actionsArray = json.optJSONArray("immediateActions") ?: JSONArray()
            for (i in 0 until actionsArray.length()) actions.add(actionsArray.getString(i))

            val soilRecs = mutableListOf<SoilRecommendation>()
            val recsArray = json.optJSONArray("soilRecommendations") ?: JSONArray()
            for (i in 0 until recsArray.length()) {
                val r = recsArray.getJSONObject(i)
                soilRecs.add(
                    SoilRecommendation(
                        parameter = r.optString("parameter", ""),
                        currentValue = r.optString("currentValue", ""),
                        targetRange = r.optString("targetRange", ""),
                        action = r.optString("action", ""),
                        priority = parseSeverity(r.optString("priority", "MEDIUM"))
                    )
                )
            }

            AnalysisResult(
                diseases = diseases,
                soilRecommendations = soilRecs,
                overallHealthScore = healthScore.coerceIn(0, 100),
                summary = json.optString("summary", response),
                immediateActions = actions.ifEmpty { listOf("Review the full analysis above") }
            )
        } catch (e: Exception) {
            Log.w(TAG, "JSON parse failed, using fallback", e)
            AnalysisResult(
                diseases = emptyList(),
                soilRecommendations = buildSoilRecommendations(soilReading),
                overallHealthScore = estimateHealthScore(soilReading),
                summary = response,
                immediateActions = listOf("Review the full analysis above")
            )
        }
    }

    private fun parseSeverity(value: String): SeverityLevel = when (value.uppercase()) {
        "LOW" -> SeverityLevel.LOW
        "HIGH" -> SeverityLevel.HIGH
        "CRITICAL" -> SeverityLevel.CRITICAL
        else -> SeverityLevel.MEDIUM
    }

    private suspend fun streamDemoResponse(
        soilReading: SoilReading?,
        withImage: Boolean,
        onToken: (String) -> Unit
    ): AnalysisResult {
        val fullResponse = buildString {
            append("FARM ASSISTANT ANALYSIS REPORT\n================================\n\n")

            if (withImage) {
                append("VISUAL DIAGNOSIS:\n")
                append("Early signs of Powdery Mildew detected. Severity: LOW to MEDIUM.\n")
                append("TREATMENT: Spray diluted neem oil (2%) every 7 days. Remove infected leaves.\n\n")
            }

            soilReading?.let { s ->
                append("SOIL ANALYSIS:\n")
                if (s.moisture < 30f) append("• Moisture critically low (${s.moisture.fmt(0)}%) — irrigate immediately.\n")
                if (s.ph < 5.5f) append("• pH ${s.ph.fmt(1)} is acidic — apply agricultural lime.\n")
                if (s.nitrogen < 25f) append("• Nitrogen low (${s.nitrogen.fmt(0)} mg/kg) — apply urea at 50 kg/ha.\n")
            }

            append("\n[DEMO MODE — Connect Gemma 4 model for real AI diagnosis]")
        }

        fullResponse.split(" ").forEachIndexed { i, word ->
            onToken(if (i == 0) word else " $word")
            kotlinx.coroutines.delay(30)
        }

        return AnalysisResult(
            diseases = if (withImage) {
                listOf(
                    DiseaseDetection(
                        "Powdery Mildew", 0.87f, "Fungal disease", SeverityLevel.MEDIUM, "Leaves"
                    )
                )
            } else emptyList(),
            soilRecommendations = buildSoilRecommendations(soilReading),
            overallHealthScore = 58,
            summary = fullResponse,
            immediateActions = listOf(
                "Apply neem oil spray within 24 hours",
                "Monitor crop daily for 7 days"
            )
        )
    }

    private fun buildDemoSoilSummary(s: SoilReading): String {
        val issues = mutableListOf<String>()
        val good = mutableListOf<String>()

        if (s.moisture < 30f) issues.add("soil is too dry (${s.moisture.fmt(0)}%) — irrigate urgently")
        else good.add("moisture is good at ${s.moisture.fmt(0)}%")

        if (s.ph < 5.5f) issues.add("soil is too acidic (pH ${s.ph.fmt(1)}) — lime treatment needed")
        else good.add("pH is healthy at ${s.ph.fmt(1)}")

        if (s.nitrogen < 25f) issues.add("nitrogen is very low (${s.nitrogen.fmt(0)} mg/kg)")

        return if (issues.isEmpty()) {
            "Your soil looks healthy overall. ${
                good.joinToString(", ").replaceFirstChar { it.uppercase() }
            }. Keep up your current soil management practices."
        } else {
            "Your soil has ${issues.size} concern(s): ${issues.joinToString("; ")}. ${
                if (good.isNotEmpty()) "On the positive side, ${good.joinToString(" and ")}. " else ""
            }Address the most critical issues before your next planting."
        }
    }

    private fun estimateHealthScore(soilReading: SoilReading?): Int {
        val s = soilReading ?: return 65
        var score = 80
        if (s.moisture < 30f || s.moisture > 80f) score -= 15
        if (s.ph < 5.5f || s.ph > 7.5f) score -= 15
        if (s.nitrogen < 25f) score -= 10
        if (s.phosphorus < 15f) score -= 5
        if (s.potassium < 90f) score -= 5
        return score.coerceIn(0, 100)
    }

    private fun buildSoilRecommendations(s: SoilReading?): List<SoilRecommendation> {
        s ?: return emptyList()
        val recs = mutableListOf<SoilRecommendation>()

        if (s.moisture < 30f) recs.add(SoilRecommendation("Moisture", "${s.moisture.fmt(0)}%", "40–60%", "Irrigate immediately", SeverityLevel.HIGH))
        if (s.moisture > 80f) recs.add(SoilRecommendation("Moisture", "${s.moisture.fmt(0)}%", "40–60%", "Improve drainage", SeverityLevel.MEDIUM))
        if (s.ph < 5.5f) recs.add(SoilRecommendation("pH", s.ph.fmt(1), "6.0–7.0", "Apply agricultural lime", SeverityLevel.HIGH))
        if (s.ph > 7.5f) recs.add(SoilRecommendation("pH", s.ph.fmt(1), "6.0–7.0", "Apply acidifying fertilizer", SeverityLevel.MEDIUM))
        if (s.nitrogen < 25f) recs.add(SoilRecommendation("Nitrogen", "${s.nitrogen.fmt(0)} mg/kg", "40–80 mg/kg", "Apply urea (46-0-0)", SeverityLevel.HIGH))
        if (s.phosphorus < 15f) recs.add(SoilRecommendation("Phosphorus", "${s.phosphorus.fmt(0)} mg/kg", "20–40 mg/kg", "Apply superphosphate", SeverityLevel.MEDIUM))
        if (s.potassium < 90f) recs.add(SoilRecommendation("Potassium", "${s.potassium.fmt(0)} mg/kg", "100–200 mg/kg", "Apply MOP fertilizer", SeverityLevel.MEDIUM))

        return recs
    }

    private fun Float.fmt(decimals: Int): String = "%.${decimals}f".format(this)

    suspend fun setLanguage(code: String) {
        currentLanguage = code
        closePersistentConversation()
        // Suspend until DataStore write completes before returning.
        // recreate() in MainActivity waits for this to finish so the
        // activity always restarts with the correct language already saved.
        context.dataStore.edit { prefs -> prefs[LANGUAGE_KEY] = code }
        Log.d(TAG, "Language set to: $code")
    }

    private fun languageInstruction(): String = when (currentLanguage) {
        "sn" -> "Respond in Shona (chiShona). Use simple farming language a Zimbabwean farmer would understand."
        "am" -> "Respond in Amharic (አማርኛ). Use simple farming language an Ethiopian farmer would understand."
        else -> "Respond in English."
    }

    fun release() {
        closePersistentConversation()
        engine?.close()
        engine = null
        _modelState.value = ModelState.NotLoaded
    }
}