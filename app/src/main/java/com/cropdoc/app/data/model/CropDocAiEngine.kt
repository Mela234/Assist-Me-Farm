package com.cropdoc.app.data.model

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import com.google.ai.edge.litertlm.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream

class CropDocAiEngine(private val context: Context) {

    companion object {
        private const val TAG = "CropDocAI"
        private const val MODEL_ASSET = "gemma-4-E4B-it.litertlm"
        private const val USE_DEMO_MODE = true
    }

    private val _modelState = MutableStateFlow<ModelState>(ModelState.NotLoaded)
    val modelState: StateFlow<ModelState> = _modelState.asStateFlow()

    private var engine: Engine? = null
    private var currentLanguage = "en"

    // ── Initialisation ────────────────────────────────────────────────────────

    suspend fun initialize() = withContext(Dispatchers.IO) {
        try {
            _modelState.value = ModelState.Loading

            if (USE_DEMO_MODE) {
                Log.d(TAG, "Demo mode — skipping real model load")
                kotlinx.coroutines.delay(2_000)
                _modelState.value = ModelState.Ready
                return@withContext
            }

            val modelFile = File(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
                ),
                MODEL_ASSET
            )

            if (!modelFile.exists()) {
                _modelState.value = ModelState.Error(
                    "Model not found. Place $MODEL_ASSET in your Downloads folder."
                )
                return@withContext
            }

            Log.d(TAG, "Loading model from ${modelFile.absolutePath}")
            val engineConfig = EngineConfig(
                modelPath = modelFile.absolutePath,
                backend = Backend.GPU(),
            )
            engine = Engine(engineConfig).also { it.initialize() }

            Log.d(TAG, "Engine ready")
            _modelState.value = ModelState.Ready

        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialise engine", e)
            _modelState.value = ModelState.Error(e.message ?: "Unknown error")
        }
    }

    // ── Crop + soil analysis ──────────────────────────────────────────────────

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

            val eng = engine ?: return@withContext Result.failure(
                IllegalStateException("Engine not initialised")
            )

            val imageFile = File(context.cacheDir, "crop_analysis.jpg")
            FileOutputStream(imageFile).use { out ->
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }

            val prompt = buildCropPrompt(soilReading)
            val conversationConfig = ConversationConfig(
                systemInstruction = Contents.of(SYSTEM_PROMPT)
            )

            var fullResponse = ""
            eng.createConversation(conversationConfig).use { conversation ->
                val message = Message.of(
                    Content.ImageFile(imageFile.absolutePath),
                    Content.Text(prompt)
                )
                conversation.sendMessageAsync(message).collect { token ->
                    val text = token.toString()
                    fullResponse += text
                    onToken(text)
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
            if (USE_DEMO_MODE) {
                return@withContext Result.success(
                    streamDemoResponse(soilReading, withImage = false, onToken)
                )
            }

            val eng = engine ?: return@withContext Result.failure(
                IllegalStateException("Engine not initialised")
            )

            val prompt = buildSoilOnlyPrompt(soilReading)
            val conversationConfig = ConversationConfig(
                systemInstruction = Contents.of(SYSTEM_PROMPT)
            )

            var fullResponse = ""
            eng.createConversation(conversationConfig).use { conversation ->
                conversation.sendMessageAsync(prompt).collect { token ->
                    val text = token.toString()
                    fullResponse += text
                    onToken(text)
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
        if (USE_DEMO_MODE) {
            return@withContext buildDemoSoilSummary(soilReading)
        }

        return@withContext try {
            val eng = engine ?: return@withContext "AI engine not ready."

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

            val conversationConfig = ConversationConfig(
                systemInstruction = Contents.of(
                    "You are a helpful farming assistant. Explain soil data in simple, plain language."
                )
            )

            var response = ""
            eng.createConversation(conversationConfig).use { conversation ->
                conversation.sendMessageAsync(prompt).collect { token ->
                    response += token.toString()
                }
            }
            response

        } catch (e: Exception) {
            Log.e(TAG, "Soil summary failed", e)
            "Could not generate summary. ${e.message}"
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
            ${'$'}{languageInstruction()}
            Analyse this crop image and respond ONLY with this exact JSON structure:
            {
              "healthScore": 75,
              "diseases": [
                {
                  "name": "Disease name",
                  "severity": "LOW",
                  "affectedArea": "Leaves",
                  "description": "Brief description"
                }
              ],
              "immediateActions": [
                "Action 1",
                "Action 2"
              ],
              "soilRecommendations": [
                {
                  "parameter": "Nitrogen",
                  "currentValue": "18 mg/kg",
                  "targetRange": "40-80 mg/kg",
                  "action": "Apply urea at 50kg/ha",
                  "priority": "HIGH"
                }
              ],
              "summary": "Plain English 2-3 sentence explanation for the farmer"
            }
            
            severity must be one of: LOW, MEDIUM, HIGH, CRITICAL
            priority must be one of: LOW, MEDIUM, HIGH, CRITICAL
            healthScore must be a number between 0 and 100
            If no diseases found, return empty array for diseases
            If no soil data, return empty array for soilRecommendations
            
            $soilSection
        """.trimIndent()
    }

    private fun buildSoilOnlyPrompt(soilReading: SoilReading): String {
        return """
            ${'$'}{languageInstruction()}
            Analyse these soil readings and respond ONLY with this exact JSON structure:
            {
              "healthScore": 55,
              "diseases": [],
              "immediateActions": [
                "Action 1",
                "Action 2"
              ],
              "soilRecommendations": [
                {
                  "parameter": "Nitrogen",
                  "currentValue": "18 mg/kg",
                  "targetRange": "40-80 mg/kg",
                  "action": "Apply urea at 50kg/ha",
                  "priority": "HIGH"
                }
              ],
              "summary": "Plain English 2-3 sentence explanation for the farmer"
            }
            
            severity must be one of: LOW, MEDIUM, HIGH, CRITICAL
            priority must be one of: LOW, MEDIUM, HIGH, CRITICAL
            healthScore must be a number between 0 and 100
            diseases must always be an empty array for soil-only analysis
            
            Soil readings:
            - Moisture: ${soilReading.moisture.fmt(0)}%
            - pH: ${soilReading.ph.fmt(1)}
            - Nitrogen: ${soilReading.nitrogen.fmt(0)} mg/kg
            - Phosphorus: ${soilReading.phosphorus.fmt(0)} mg/kg
            - Potassium: ${soilReading.potassium.fmt(0)} mg/kg
            - Temperature: ${soilReading.temperature.fmt(1)}°C
        """.trimIndent()
    }

    // ── JSON Response Parser ──────────────────────────────────────────────────

    private fun parseJsonResponse(
        response: String,
        soilReading: SoilReading?
    ): AnalysisResult {
        return try {
            // Strip any markdown code fences Gemma might add despite instructions
            val cleaned = response
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val json = JSONObject(cleaned)

            val healthScore = json.optInt("healthScore", estimateHealthScore(soilReading))

            // Parse diseases
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

            // Parse immediate actions
            val actions = mutableListOf<String>()
            val actionsArray = json.optJSONArray("immediateActions") ?: JSONArray()
            for (i in 0 until actionsArray.length()) {
                actions.add(actionsArray.getString(i))
            }

            // Parse soil recommendations
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

            val summary = json.optString("summary", response)

            AnalysisResult(
                diseases = diseases,
                soilRecommendations = soilRecs,
                overallHealthScore = healthScore.coerceIn(0, 100),
                summary = summary,
                immediateActions = actions.ifEmpty {
                    listOf("Follow the recommendations above")
                }
            )

        } catch (e: Exception) {
            // JSON parsing failed — fall back to rule-based result
            Log.w(TAG, "JSON parse failed, using fallback. Response was: $response", e)
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
        "LOW"      -> SeverityLevel.LOW
        "HIGH"     -> SeverityLevel.HIGH
        "CRITICAL" -> SeverityLevel.CRITICAL
        else       -> SeverityLevel.MEDIUM
    }

    // ── Demo response streamer ────────────────────────────────────────────────

    private suspend fun streamDemoResponse(
        soilReading: SoilReading?,
        withImage: Boolean,
        onToken: (String) -> Unit
    ): AnalysisResult {

        val soilSection = soilReading?.let { s ->
            buildString {
                appendLine()
                appendLine("SOIL ANALYSIS:")
                if (s.moisture < 30f)
                    appendLine("• Moisture at ${s.moisture.fmt(0)}% is critically low — immediate irrigation needed.")
                else if (s.moisture > 75f)
                    appendLine("• Moisture at ${s.moisture.fmt(0)}% is too high — improve drainage.")
                else
                    appendLine("• Moisture at ${s.moisture.fmt(0)}% is within acceptable range.")
                if (s.ph < 5.5f)
                    appendLine("• pH ${s.ph.fmt(1)} is acidic. Apply agricultural lime to raise it toward 6.0–7.0.")
                else if (s.ph > 7.5f)
                    appendLine("• pH ${s.ph.fmt(1)} is alkaline. Apply sulfur or acidifying fertilizer.")
                else
                    appendLine("• pH ${s.ph.fmt(1)} is in the ideal range.")
                if (s.nitrogen < 25f)
                    appendLine("• Nitrogen at ${s.nitrogen.fmt(0)} mg/kg is deficient. Apply urea (46-0-0) at 50 kg/ha.")
                if (s.phosphorus < 15f)
                    appendLine("• Phosphorus is low. Apply superphosphate at 30 kg/ha.")
                if (s.potassium < 90f)
                    appendLine("• Potassium is below optimal. Apply MOP (muriate of potash) at 40 kg/ha.")
            }
        } ?: ""

        val imageSection = if (withImage) """
VISUAL DIAGNOSIS:
The crop image shows early signs of Powdery Mildew — a fungal disease identifiable by the
white powdery coating appearing on the upper leaf surfaces. Affected leaves show mild
yellowing around the margins. Severity is currently LOW to MEDIUM.

TREATMENT:
- Organic: Spray diluted neem oil (2%) every 7 days, or a baking soda solution (1 tbsp per litre).
- Conventional: Apply fungicide containing myclobutanil or sulfur-based spray.
- Remove and destroy heavily infected leaves immediately to prevent spread.
- Ensure adequate plant spacing for airflow — mildew thrives in humid, stagnant conditions.

""" else ""

        val fullResponse = buildString {
            append("CROPDOC ANALYSIS REPORT\n")
            append("========================\n\n")
            if (withImage) append(imageSection)
            if (soilSection.isNotEmpty()) append(soilSection)
            append("\nIMMEDIATE ACTIONS:\n")
            append("→ Apply neem oil spray within 24 hours\n")
            if (soilReading != null && soilReading.moisture < 30f)
                append("→ Irrigate immediately — soil moisture is critically low\n")
            if (soilReading != null && soilReading.ph < 5.5f)
                append("→ Apply lime to correct soil acidity this week\n")
            if (soilReading != null && soilReading.nitrogen < 25f)
                append("→ Apply nitrogen fertilizer before next watering\n")
            append("→ Monitor crop daily for the next 7 days\n")
            append("→ Re-scan after treatment to track recovery\n")
            append("\n[DEMO MODE — Connect Gemma 4 model for real AI diagnosis]")
        }

        val words = fullResponse.split(" ")
        words.forEachIndexed { i, word ->
            val token = if (i == 0) word else " $word"
            onToken(token)
            kotlinx.coroutines.delay(30)
        }

        val hasMildew = withImage
        val hasLowMoisture = soilReading?.let { it.moisture < 30f } ?: false
        val hasAcidPh = soilReading?.let { it.ph < 5.5f } ?: false
        val hasLowN = soilReading?.let { it.nitrogen < 25f } ?: false

        val healthScore = when {
            hasMildew && hasLowMoisture && hasAcidPh -> 28
            hasMildew && (hasLowMoisture || hasAcidPh) -> 42
            hasMildew -> 58
            hasLowMoisture || hasAcidPh -> 55
            else -> 78
        }

        val diseases = if (hasMildew) listOf(
            DiseaseDetection(
                name = "Powdery Mildew",
                confidence = 0.87f,
                description = "Fungal disease — white powdery coating on upper leaf surfaces",
                severity = SeverityLevel.MEDIUM,
                affectedArea = "Leaves"
            )
        ) else emptyList()

        val actions = mutableListOf("Apply neem oil spray within 24 hours").apply {
            if (hasLowMoisture) add("Irrigate immediately")
            if (hasAcidPh) add("Apply agricultural lime this week")
            if (hasLowN) add("Apply nitrogen fertilizer before next watering")
            add("Monitor crop daily for 7 days")
        }

        return AnalysisResult(
            diseases = diseases,
            soilRecommendations = buildSoilRecommendations(soilReading),
            overallHealthScore = healthScore,
            summary = fullResponse,
            immediateActions = actions
        )
    }

    // ── Demo soil summary ─────────────────────────────────────────────────────

    private fun buildDemoSoilSummary(s: SoilReading): String {
        val issues = mutableListOf<String>()
        val good = mutableListOf<String>()

        if (s.moisture < 30f) issues.add("your soil is too dry (${s.moisture.fmt(0)}% moisture) — crops need water urgently")
        else if (s.moisture > 75f) issues.add("your soil is waterlogged (${s.moisture.fmt(0)}%) — improve drainage")
        else good.add("moisture is good at ${s.moisture.fmt(0)}%")

        if (s.ph < 5.5f) issues.add("the soil is too acidic (pH ${s.ph.fmt(1)}) — lime treatment needed")
        else if (s.ph > 7.5f) issues.add("the soil is too alkaline (pH ${s.ph.fmt(1)}) — add sulfur")
        else good.add("pH is healthy at ${s.ph.fmt(1)}")

        if (s.nitrogen < 25f) issues.add("nitrogen is very low (${s.nitrogen.fmt(0)} mg/kg) — plants will struggle to grow")
        if (s.phosphorus < 15f) issues.add("phosphorus is low — root development will be poor")
        if (s.potassium < 90f) issues.add("potassium is below optimal — affects fruit and disease resistance")

        return buildString {
            if (issues.isEmpty()) {
                append("Your soil looks healthy overall. ")
                append(good.joinToString(", ").replaceFirstChar { it.uppercase() })
                append(". Keep up your current soil management practices.")
            } else {
                append("Your soil has ${issues.size} concern(s): ")
                append(issues.joinToString("; "))
                append(". ")
                if (good.isNotEmpty()) {
                    append("On the positive side, ${good.joinToString(" and ")}. ")
                }
                append("Address the most critical issues before your next planting.")
            }
        }
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
        if (s.moisture < 30f) recs.add(SoilRecommendation("Moisture",
            "${s.moisture.fmt(0)}%", "40–60%", "Irrigate immediately", SeverityLevel.HIGH))
        if (s.moisture > 80f) recs.add(SoilRecommendation("Moisture",
            "${s.moisture.fmt(0)}%", "40–60%", "Improve drainage", SeverityLevel.MEDIUM))
        if (s.ph < 5.5f) recs.add(SoilRecommendation("pH",
            s.ph.fmt(1), "6.0–7.0", "Apply agricultural lime", SeverityLevel.HIGH))
        if (s.ph > 7.5f) recs.add(SoilRecommendation("pH",
            s.ph.fmt(1), "6.0–7.0", "Apply acidifying fertilizer", SeverityLevel.MEDIUM))
        if (s.nitrogen < 25f) recs.add(SoilRecommendation("Nitrogen",
            "${s.nitrogen.fmt(0)} mg/kg", "40–80 mg/kg", "Apply urea (46-0-0)", SeverityLevel.HIGH))
        if (s.phosphorus < 15f) recs.add(SoilRecommendation("Phosphorus",
            "${s.phosphorus.fmt(0)} mg/kg", "20–40 mg/kg", "Apply superphosphate", SeverityLevel.MEDIUM))
        if (s.potassium < 90f) recs.add(SoilRecommendation("Potassium",
            "${s.potassium.fmt(0)} mg/kg", "100–200 mg/kg", "Apply MOP fertilizer", SeverityLevel.MEDIUM))
        return recs
    }

    private fun Float.fmt(decimals: Int) = "%.${decimals}f".format(this)

    fun release() {
        engine?.close()
        engine = null
        _modelState.value = ModelState.NotLoaded
    }

    fun setLanguage(code: String) {
        currentLanguage = code
    }

    private fun languageInstruction(): String = when (currentLanguage) {
        "sn" -> "Respond in Shona (chiShona). Use simple farming language a Zimbabwean farmer would understand."
        "am" -> "Respond in Amharic (አማርኛ). Use simple farming language an Ethiopian farmer would understand."
        else -> "Respond in English."
    }
}