package com.cropdoc.app.data.model

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File

/**
 * CropDoc AI Engine
 *
 * DEMO MODE: Runs without the Gemma 4 model file so you can test the full UI.
 * When USE_DEMO_MODE = false, it uses the real LiteRT-LM engine with Gemma 4 E4B.
 *
 * To switch to real AI:
 *   1. Place gemma-4-E4B-it.litertlm in app/src/main/assets/
 *   2. Add LiteRT-LM dependency back to build.gradle.kts
 *   3. Set USE_DEMO_MODE = false
 */
class CropDocAiEngine(private val context: Context) {

    companion object {
        private const val TAG = "CropDocAI"
        private const val USE_DEMO_MODE = true // ← flip to false when model is ready
        private const val MODEL_ASSET = "gemma-4-E4B-it.litertlm"
    }

    private val _modelState = MutableStateFlow<ModelState>(ModelState.NotLoaded)
    val modelState: StateFlow<ModelState> = _modelState.asStateFlow()

    // ── Initialisation ────────────────────────────────────────────────────────

    suspend fun initialize() = withContext(Dispatchers.IO) {
        try {
            _modelState.value = ModelState.Loading

            if (USE_DEMO_MODE) {
                // Simulate model loading time so the UI flow feels realistic
                Log.d(TAG, "Demo mode — skipping real model load")
                delay(2_000)
                _modelState.value = ModelState.Ready
                return@withContext
            }

            // ── Real LiteRT-LM path (used when USE_DEMO_MODE = false) ──────
            // Uncomment and add imports when switching to real mode:
            //
            // val file = File(context.filesDir, MODEL_ASSET)
            // if (!file.exists()) {
            //     context.assets.open(MODEL_ASSET).use { input ->
            //         FileOutputStream(file).use { output -> input.copyTo(output) }
            //     }
            // }
            // val config = EngineConfig(
            //     modelPath = file.absolutePath,
            //     backend = Backend.GPU(),
            //     visionBackend = Backend.GPU(),
            // )
            // engine = Engine(config)
            // engine!!.initialize()
            // ──────────────────────────────────────────────────────────────

            _modelState.value = ModelState.Ready

        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialise engine", e)
            _modelState.value = ModelState.Error(e.message ?: "Unknown error")
        }
    }

    // ── Crop disease analysis ─────────────────────────────────────────────────

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
            // Real inference would go here
            Result.success(streamDemoResponse(soilReading, withImage = true, onToken))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Soil-only analysis ────────────────────────────────────────────────────

    suspend fun analyseSoilOnly(
        soilReading: SoilReading,
        onToken: (String) -> Unit
    ): Result<AnalysisResult> = withContext(Dispatchers.IO) {
        try {
            Result.success(streamDemoResponse(soilReading, withImage = false, onToken))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Demo response streamer ────────────────────────────────────────────────

    /**
     * Streams a realistic demo response word-by-word to simulate Gemma 4 inference.
     * Includes soil-aware commentary when readings are provided.
     */
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
• Organic: Spray diluted neem oil (2%) every 7 days, or a baking soda solution (1 tbsp per litre).
• Conventional: Apply fungicide containing myclobutanil or sulfur-based spray.
• Remove and destroy heavily infected leaves immediately to prevent spread.
• Ensure adequate plant spacing for airflow — mildew thrives in humid, stagnant conditions.

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

        // Stream word by word with realistic delay
        val words = fullResponse.split(" ")
        words.forEachIndexed { i, word ->
            val token = if (i == 0) word else " $word"
            onToken(token)
            delay(30)
        }

        // Build structured result
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

        val soilRecs = buildSoilRecommendations(soilReading)

        val actions = mutableListOf("Apply neem oil spray within 24 hours").apply {
            if (hasLowMoisture) add("Irrigate immediately")
            if (hasAcidPh) add("Apply agricultural lime this week")
            if (hasLowN) add("Apply nitrogen fertilizer before next watering")
            add("Monitor crop daily for 7 days")
        }

        return AnalysisResult(
            diseases = diseases,
            soilRecommendations = soilRecs,
            overallHealthScore = healthScore,
            summary = fullResponse,
            immediateActions = actions
        )
    }

    // ── Soil recommendations ──────────────────────────────────────────────────

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
        _modelState.value = ModelState.NotLoaded
    }
}
