package com.cropdoc.app.data.model

// ── Soil sensor data ──────────────────────────────────────────────────────────

data class SoilReading(
    val moisture: Float,        // % (0–100)
    val ph: Float,              // pH (0–14)
    val nitrogen: Float,        // mg/kg
    val phosphorus: Float,      // mg/kg
    val potassium: Float,       // mg/kg
    val temperature: Float,     // °C
    val timestamp: Long = System.currentTimeMillis()
)

// ── BLE connection state ──────────────────────────────────────────────────────

sealed class BleState {
    object Disconnected : BleState()
    object Scanning : BleState()
    data class Connecting(val deviceName: String) : BleState()
    data class Connected(val deviceName: String, val deviceAddress: String) : BleState()
    data class Error(val message: String) : BleState()
}

data class BleDevice(
    val name: String,
    val address: String,
    val rssi: Int,
    val isCropDocSensor: Boolean = false
)

// ── AI Analysis ──────────────────────────────────────────────────────────────

enum class SeverityLevel { LOW, MEDIUM, HIGH, CRITICAL }

data class DiseaseDetection(
    val name: String,
    val confidence: Float,        // 0–1
    val description: String,
    val severity: SeverityLevel,
    val affectedArea: String      // e.g. "Leaves", "Stems", "Roots"
)

data class SoilRecommendation(
    val parameter: String,        // e.g. "Nitrogen"
    val currentValue: String,
    val targetRange: String,
    val action: String,           // e.g. "Apply urea fertilizer"
    val priority: SeverityLevel
)

data class AnalysisResult(
    val diseases: List<DiseaseDetection>,
    val soilRecommendations: List<SoilRecommendation>,
    val overallHealthScore: Int,  // 0–100
    val summary: String,
    val immediateActions: List<String>,
    val timestamp: Long = System.currentTimeMillis()
)


// ── AI Engine state ───────────────────────────────────────────────────────────

sealed class ModelState {
    object NotLoaded : ModelState()
    object Loading : ModelState()
    object Ready : ModelState()
    data class Error(val message: String) : ModelState()
}

sealed class AnalysisState {
    object Idle : AnalysisState()
    object Analyzing : AnalysisState()
    data class Complete(val result: AnalysisResult) : AnalysisState()
    data class Error(val message: String) : AnalysisState()
}
