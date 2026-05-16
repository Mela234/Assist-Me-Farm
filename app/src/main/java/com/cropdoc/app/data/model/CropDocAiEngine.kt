package com.cropdoc.app.data.model

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.cropdoc.app.data.agent.CropDocToolSet
import com.google.ai.edge.litertlm.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

private enum class EngineModality { TEXT, VISION, AUDIO }

class CropDocAiEngine(private val context: Context) {

    companion object {
        private const val TAG = "CropDocAI"
        private const val MODEL_ASSET = "gemma-4-E2B-it.litertlm"
        private const val USE_DEMO_MODE = false
    }

    private val _modelState = MutableStateFlow<ModelState>(ModelState.NotLoaded)
    val modelState: StateFlow<ModelState> = _modelState.asStateFlow()

    private var engine: Engine? = null
    private var currentModality: EngineModality? = null
    private val engineMutex = Mutex()

    private var currentLanguage = "en"

    // ── Initialisation ────────────────────────────────────────────────────────

    suspend fun initialize() = withContext(Dispatchers.IO) {
        try {
            _modelState.value = ModelState.Loading

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

            engine = buildEngine(modelFile.absolutePath, EngineModality.TEXT)
                .also { it.initialize() }
            currentModality = EngineModality.TEXT

            _modelState.value = ModelState.Ready

        } catch (e: Exception) {
            _modelState.value = ModelState.Error(e.message ?: "Unknown error")
        }
    }

    private fun buildEngine(modelPath: String, modality: EngineModality): Engine {
        val config = when (modality) {
            EngineModality.TEXT -> EngineConfig(
                modelPath = modelPath,
                backend = Backend.GPU(),
                visionBackend = null,
                audioBackend = null,
                maxNumTokens = 4096,
                cacheDir = context.cacheDir.absolutePath
            )
            EngineModality.VISION -> EngineConfig(
                modelPath = modelPath,
                backend = Backend.GPU(),
                visionBackend = Backend.CPU(),
                audioBackend = null,
                maxNumTokens = 4096,
                cacheDir = context.cacheDir.absolutePath
            )
            EngineModality.AUDIO -> EngineConfig(
                modelPath = modelPath,
                backend = Backend.GPU(),
                visionBackend = null,
                audioBackend = Backend.CPU(),
                maxNumTokens = 4096,
                cacheDir = context.cacheDir.absolutePath
            )
        }
        return Engine(config)
    }

    private suspend fun ensureEngine(modality: EngineModality) {
        if (currentModality == modality && engine != null) return

        Log.d(TAG, "Switching engine: $currentModality → $modality")
        engine?.close()
        engine = null
        currentModality = null

        val modelFile = File(context.filesDir, MODEL_ASSET)
        val newEngine = buildEngine(modelFile.absolutePath, modality)
        newEngine.initialize()
        engine = newEngine
        currentModality = modality
        Log.d(TAG, "$modality engine ready")
    }

    // ── Crop + soil analysis ──────────────────────────────────────────────────

    suspend fun analyseCrop(
        imageBitmap: Bitmap,
        soilReading: SoilReading?,
        onToken: (String) -> Unit
    ): Result<AnalysisResult> = withContext(Dispatchers.IO) {
        try {
            if (USE_DEMO_MODE) return@withContext Result.success(
                streamDemoResponse(soilReading, withImage = true, onToken))

            val imageFile = File(context.cacheDir, "crop_analysis.jpg")
            FileOutputStream(imageFile).use { out ->
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }

            val prompt = buildCropPrompt(soilReading)
            var fullResponse = ""

            engineMutex.withLock {
                ensureEngine(EngineModality.VISION)
                val eng = engine ?: return@withLock
                val cfg = ConversationConfig(systemInstruction = Contents.of(SYSTEM_PROMPT))
                eng.createConversation(cfg).use { conv ->
                    conv.sendMessageAsync(
                        Message.of(Content.ImageFile(imageFile.absolutePath), Content.Text(prompt))
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

    // ── Soil-only analysis ────────────────────────────────────────────────────

    suspend fun analyseSoilOnly(
        soilReading: SoilReading,
        onToken: (String) -> Unit
    ): Result<AnalysisResult> = withContext(Dispatchers.IO) {
        try {
            if (USE_DEMO_MODE) return@withContext Result.success(
                streamDemoResponse(soilReading, withImage = false, onToken))

            var fullResponse = ""
            engineMutex.withLock {
                ensureEngine(EngineModality.TEXT)
                val eng = engine ?: return@withLock
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

    // ── Soil plain-English summary ────────────────────────────────────────────

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
                ensureEngine(EngineModality.TEXT)
                val eng = engine ?: return@withLock
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

    // ── Chat ──────────────────────────────────────────────────────────────────

    suspend fun chat(
        userMessage: String,
        imageUri: Uri?,
        audioFile: File? = null,
        soilReading: SoilReading?,
        weatherData: WeatherData?,
        zone: FarmZone?,
        crop: Crop? = null,
        allZonesWithCrops: List<Pair<FarmZone, Crop?>> = emptyList(), // ← for general chat
        history: List<ChatMessage>,
        context: Context,
        onToken: (String) -> Unit
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (USE_DEMO_MODE) return@withContext Result.success(
                buildDemoChatResponse(userMessage, soilReading, weatherData, zone).also { response ->
                    response.split(" ").forEachIndexed { i, word ->
                        onToken(if (i == 0) word else " $word")
                        kotlinx.coroutines.delay(25)
                    }
                }
            )

            val modality = when {
                imageUri != null -> EngineModality.VISION
                audioFile != null -> EngineModality.AUDIO
                else -> EngineModality.TEXT
            }

            val systemPrompt = buildChatSystemPrompt(
                soilReading, weatherData, zone, crop, allZonesWithCrops
            )
            var fullResponse = ""

            engineMutex.withLock {
                ensureEngine(modality)
                val eng = engine ?: return@withLock

                val cfg = ConversationConfig(
                    systemInstruction = Contents.of(systemPrompt),
                    tools = listOf(tool(CropDocToolSet(context))),
                    automaticToolCalling = true
                )

                eng.createConversation(cfg).use { conv ->
                    history.dropLast(1).forEach { msg ->
                        if (msg.role == "USER") {
                            conv.sendMessageAsync(msg.content).collect {}
                        }
                    }

                    val finalMessage = when (modality) {
                        EngineModality.VISION -> {
                            val imgFile = when {
                                imageUri!!.scheme == "file" -> File(imageUri.path!!)
                                else -> {
                                    val f = File(context.cacheDir, "chat_image.jpg")
                                    context.contentResolver.openInputStream(imageUri)?.use { input ->
                                        FileOutputStream(f).use { output -> input.copyTo(output) }
                                    }
                                    f
                                }
                            }

                            Log.d(TAG, "Image file: ${imgFile.absolutePath}, exists: ${imgFile.exists()}, size: ${imgFile.length()}")

                            if (!imgFile.exists() || imgFile.length() == 0L) {
                                Log.w(TAG, "Image file missing or empty, falling back to text")
                                Message.of(Content.Text("$userMessage [Note: I could not read the attached image]"))
                            } else {
                                Log.d(TAG, "Building vision message...")
                                val msg = Message.of(Content.ImageFile(imgFile.absolutePath), Content.Text(userMessage))
                                Log.d(TAG, "Vision message built, sending to engine...")
                                msg
                            }
                        }
                        EngineModality.AUDIO -> {
                            Message.of(Content.AudioFile(audioFile!!.absolutePath), Content.Text(userMessage))
                        }
                        EngineModality.TEXT -> {
                            Message.of(Content.Text(userMessage))
                        }
                    }

                    Log.d(TAG, "About to send message, modality: $modality")
                    try {
                        conv.sendMessageAsync(finalMessage).collect { token ->
                            val text = token.toString()
                            fullResponse += text
                            onToken(text)
                        }
                        Log.d(TAG, "Message sent successfully, response length: ${fullResponse.length}")
                    } catch (e: Exception) {
                        Log.e(TAG, "sendMessageAsync failed: ${e.message}", e)
                        conv.sendMessageAsync(userMessage).collect { token ->
                            val text = token.toString()
                            fullResponse += text
                            onToken(text)
                        }
                    }
                }
            }

            Result.success(fullResponse)

        } catch (e: Exception) {
            Log.e(TAG, "Chat failed", e)
            Result.failure(e)
        }
    }

    // ── Agent check ───────────────────────────────────────────────────────────

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
                ensureEngine(EngineModality.TEXT)
                val eng = engine ?: return@withLock
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
            val jsonArray = JSONArray(cleaned)
            for (i in 0 until jsonArray.length()) onAlert(jsonArray.getString(i))
        } catch (e: Exception) {
            Log.e(TAG, "Agent check failed", e)
        }
    }

    // ── Chat system prompt ────────────────────────────────────────────────────

    private fun buildChatSystemPrompt(
        soilReading: SoilReading?,
        weatherData: WeatherData?,
        zone: FarmZone?,
        crop: Crop? = null,
        allZonesWithCrops: List<Pair<FarmZone, Crop?>> = emptyList()
    ): String = buildString {
        appendLine(languageInstruction())
        appendLine()

        when {
            // ── Zone chat with a known crop ───────────────────────────────────
            zone != null && crop != null -> {
                val daysPlanted = ((System.currentTimeMillis() - crop.plantedDate) / (1000 * 60 * 60 * 24)).toInt()
                val daysToHarvest = (crop.expectedHarvestDays - daysPlanted).coerceAtLeast(0)
                appendLine("""
                You are CropDoc, a farming assistant helping a smallholder farmer in Africa.
                You are currently helping them manage their ${crop.name} field called "${zone.name}".
                The ${crop.name} was planted $daysPlanted days ago and is expected to be ready
                for harvest in $daysToHarvest days (${crop.expectedHarvestDays} days total from planting).
                ${if (crop.notes.isNotBlank()) "Farmer's notes about this field: ${crop.notes}" else ""}
                All advice you give should be specific to ${crop.name} at this growth stage.
                Be conversational and practical — you are talking to a farmer in the field.
            """.trimIndent())
            }

            // ── Zone chat with no crop recorded ──────────────────────────────
            zone != null -> {
                appendLine("""
                You are CropDoc, a farming assistant helping a smallholder farmer in Africa.
                You are currently helping them manage their farm zone called "${zone.name}".
                No crop has been recorded for this zone yet — advise them on what to plant
                or how to prepare the soil based on the readings available.
                Be conversational and practical — you are talking to a farmer in the field.
            """.trimIndent())
            }

            // ── General chat — describe the whole farm ────────────────────────
            allZonesWithCrops.isNotEmpty() -> {
                appendLine("""
                You are CropDoc, a farming assistant helping a smallholder farmer in Africa.
                You are their general farm assistant. Here is their current farm setup:
            """.trimIndent())
                appendLine()
                allZonesWithCrops.forEachIndexed { index, (z, c) ->
                    if (c != null) {
                        val daysPlanted = ((System.currentTimeMillis() - c.plantedDate) / (1000 * 60 * 60 * 24)).toInt()
                        val daysToHarvest = (c.expectedHarvestDays - daysPlanted).coerceAtLeast(0)
                        appendLine("- Zone ${index + 1}: \"${z.name}\" — ${c.name}, planted $daysPlanted days ago, harvest in $daysToHarvest days")
                    } else {
                        appendLine("- Zone ${index + 1}: \"${z.name}\" — no crop planted yet")
                    }
                }
                appendLine()
                appendLine("""
                Give advice relevant to their specific crops and farm setup.
                If the farmer asks about a specific zone or crop, give targeted advice for that crop.
                Be conversational and practical — you are talking to a farmer in the field.
            """.trimIndent())
            }

            // ── General chat — no zones set up yet ───────────────────────────
            else -> {
                appendLine("""
                You are CropDoc, a friendly and knowledgeable farming assistant
                helping smallholder farmers in Africa. You have deep expertise in
                agronomy, crop diseases, soil health, and sustainable farming practices.
                Give practical, actionable advice using simple language.
                Be conversational and warm — you are talking to a farmer in the field.
            """.trimIndent())
            }
        }

        // Soil context
        soilReading?.let {
            appendLine()
            appendLine("Current soil readings from the sensor:")
            appendLine("- Moisture: ${it.moisture.fmt(0)}%")
            appendLine("- pH: ${it.ph.fmt(1)}")
            appendLine("- Nitrogen: ${it.nitrogen.fmt(0)} mg/kg")
            appendLine("- Phosphorus: ${it.phosphorus.fmt(0)} mg/kg")
            appendLine("- Potassium: ${it.potassium.fmt(0)} mg/kg")
            appendLine("- Soil temperature: ${it.temperature.fmt(1)}°C")
        }

        // Weather context
        weatherData?.let {
            appendLine()
            appendLine("Current weather at ${it.location}:")
            appendLine("- Temperature: ${it.temperature}°C")
            appendLine("- Humidity: ${it.humidity}%")
            appendLine("- Expected rainfall: ${it.rainfall}mm")
            appendLine("- Wind speed: ${it.windSpeed} km/h")
            appendLine("- Forecast: ${it.forecast}")
        }

        // Input types instruction — always last
        appendLine()
        appendLine("""
        The farmer can send you:
        - Text messages — answer directly
        - Images of crops or soil — visually diagnose diseases, pests, or deficiencies
        - Voice messages — the audio will be transcribed and treated as a text question
        When an image is provided, always comment on what you visually observe before giving advice.
        When a voice message is provided, respond naturally as if in a conversation.
    """.trimIndent())
    }

    // ── Demo responses ────────────────────────────────────────────────────────

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
                if (moisture != null && moisture < 30f)
                    "Your soil moisture is critically low at ${moisture.fmt(0)}%. Irrigate immediately — aim for at least 25mm of water. Early morning irrigation is best to reduce evaporation."
                else if (moisture != null)
                    "Your soil moisture is at ${moisture.fmt(0)}%, which is ${if (moisture in 30f..70f) "within the healthy range. No irrigation needed right now." else "slightly high. Ensure good drainage to prevent root rot."}"
                else
                    "Without soil readings I can't give you a precise answer. As a general rule, most crops need irrigation when the top 5cm of soil feels dry to the touch."
            }
            lower.contains("fertiliz") || lower.contains("nitrogen") || lower.contains("npk") -> {
                val n = soilReading?.nitrogen
                if (n != null && n < 25f)
                    "Your nitrogen level is low at ${n.fmt(0)} mg/kg — the ideal range is 40-80 mg/kg. Apply urea (46-0-0) at 50 kg per hectare. Split the application — half now, half in 3 weeks."
                else
                    "Your nitrogen levels look adequate. Focus on maintaining organic matter by incorporating crop residues after harvest."
            }
            lower.contains("disease") || lower.contains("pest") || lower.contains("sick") ->
                "I can help diagnose crop diseases! For the most accurate diagnosis, take a photo of the affected area and include it in your message."
            else -> buildString {
                append("I'm here to help with your farm! ")
                soilReading?.let {
                    if (it.moisture < 30f) append("Your soil needs water urgently. ")
                    if (it.ph < 5.5f) append("Your pH is too acidic. ")
                    if (it.nitrogen < 25f) append("Nitrogen is low. ")
                    if (it.moisture >= 30f && it.ph in 5.5f..7.5f && it.nitrogen >= 25f) append("Everything looks fairly healthy! ")
                }
                append("Ask me about watering, fertilizers, crop diseases, or harvest timing.")
            }
        }
    }

    // ── Prompts ───────────────────────────────────────────────────────────────

    private val SYSTEM_PROMPT = """
        You are CropDoc, an expert agronomist AI assistant helping smallholder farmers.
        You MUST respond ONLY with valid JSON. No explanation, no markdown, no extra text.
        Just the raw JSON object.
    """.trimIndent()

    private fun buildCropPrompt(soilReading: SoilReading?): String {
        val soilSection = soilReading?.let {
            "Soil readings:\n- Moisture: ${it.moisture.fmt(0)}%\n- pH: ${it.ph.fmt(1)}\n- Nitrogen: ${it.nitrogen.fmt(0)} mg/kg\n- Phosphorus: ${it.phosphorus.fmt(0)} mg/kg\n- Potassium: ${it.potassium.fmt(0)} mg/kg\n- Temperature: ${it.temperature.fmt(1)}°C"
        } ?: "No soil data available."
        return """
            ${languageInstruction()}
            Analyse this crop image and respond ONLY with this exact JSON structure:
            {"healthScore":75,"diseases":[{"name":"Disease name","severity":"LOW","affectedArea":"Leaves","description":"Brief description"}],"immediateActions":["Action 1"],"soilRecommendations":[{"parameter":"Nitrogen","currentValue":"18 mg/kg","targetRange":"40-80 mg/kg","action":"Apply urea at 50kg/ha","priority":"HIGH"}],"summary":"Plain English explanation"}
            severity and priority must be one of: LOW, MEDIUM, HIGH, CRITICAL
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

    // ── JSON parser ───────────────────────────────────────────────────────────

    private fun parseJsonResponse(response: String, soilReading: SoilReading?): AnalysisResult {
        return try {
            val cleaned = response.replace("```json", "").replace("```", "").trim()
            val json = JSONObject(cleaned)
            val healthScore = json.optInt("healthScore", estimateHealthScore(soilReading))
            val diseases = mutableListOf<DiseaseDetection>()
            val diseasesArray = json.optJSONArray("diseases") ?: JSONArray()
            for (i in 0 until diseasesArray.length()) {
                val d = diseasesArray.getJSONObject(i)
                diseases.add(DiseaseDetection(
                    name = d.optString("name", "Unknown"),
                    confidence = 0.9f,
                    description = d.optString("description", ""),
                    severity = parseSeverity(d.optString("severity", "MEDIUM")),
                    affectedArea = d.optString("affectedArea", "Unknown")
                ))
            }
            val actions = mutableListOf<String>()
            val actionsArray = json.optJSONArray("immediateActions") ?: JSONArray()
            for (i in 0 until actionsArray.length()) actions.add(actionsArray.getString(i))
            val soilRecs = mutableListOf<SoilRecommendation>()
            val recsArray = json.optJSONArray("soilRecommendations") ?: JSONArray()
            for (i in 0 until recsArray.length()) {
                val r = recsArray.getJSONObject(i)
                soilRecs.add(SoilRecommendation(
                    parameter = r.optString("parameter", ""),
                    currentValue = r.optString("currentValue", ""),
                    targetRange = r.optString("targetRange", ""),
                    action = r.optString("action", ""),
                    priority = parseSeverity(r.optString("priority", "MEDIUM"))
                ))
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

    // ── Demo streamer ─────────────────────────────────────────────────────────

    private suspend fun streamDemoResponse(
        soilReading: SoilReading?,
        withImage: Boolean,
        onToken: (String) -> Unit
    ): AnalysisResult {
        val fullResponse = buildString {
            append("CROPDOC ANALYSIS REPORT\n========================\n\n")
            if (withImage) append("VISUAL DIAGNOSIS:\nEarly signs of Powdery Mildew detected. Severity: LOW to MEDIUM.\nTREATMENT: Spray diluted neem oil (2%) every 7 days. Remove infected leaves.\n\n")
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
            diseases = if (withImage) listOf(DiseaseDetection("Powdery Mildew", 0.87f, "Fungal disease", SeverityLevel.MEDIUM, "Leaves")) else emptyList(),
            soilRecommendations = buildSoilRecommendations(soilReading),
            overallHealthScore = 58,
            summary = fullResponse,
            immediateActions = listOf("Apply neem oil spray within 24 hours", "Monitor crop daily for 7 days")
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
        return if (issues.isEmpty())
            "Your soil looks healthy overall. ${good.joinToString(", ").replaceFirstChar { it.uppercase() }}. Keep up your current soil management practices."
        else
            "Your soil has ${issues.size} concern(s): ${issues.joinToString("; ")}. ${if (good.isNotEmpty()) "On the positive side, ${good.joinToString(" and ")}. " else ""}Address the most critical issues before your next planting."
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

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

    private fun Float.fmt(decimals: Int) = "%.${decimals}f".format(this)

    fun setLanguage(code: String) { currentLanguage = code }

    private fun languageInstruction(): String = when (currentLanguage) {
        "sn" -> "Respond in Shona (chiShona). Use simple farming language a Zimbabwean farmer would understand."
        "am" -> "Respond in Amharic (አማርኛ). Use simple farming language an Ethiopian farmer would understand."
        else -> "Respond in English."
    }

    fun release() {
        engine?.close()
        engine = null
        currentModality = null
        _modelState.value = ModelState.NotLoaded
    }
}