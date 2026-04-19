package com.cropdoc.app.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cropdoc.app.data.ble.SoilSensorBleManager
import com.cropdoc.app.data.model.AnalysisResult
import com.cropdoc.app.data.model.AnalysisState
import com.cropdoc.app.data.model.BleState
import com.cropdoc.app.data.model.CropDocAiEngine
import com.cropdoc.app.data.model.ModelState
import com.cropdoc.app.data.model.SoilReading
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class CropDocViewModel(application: Application) : AndroidViewModel(application) {

    private val aiEngine = CropDocAiEngine(application)
    val bleManager = SoilSensorBleManager(application)

    // ── Model state ───────────────────────────────────────────────────────────
    val modelState: StateFlow<ModelState> = aiEngine.modelState

    // ── BLE state ─────────────────────────────────────────────────────────────
    val bleState: StateFlow<BleState> = bleManager.bleState
    val scannedDevices = bleManager.scannedDevices

    // Merged soil reading — real BLE or mock
    private val _soilReading = MutableStateFlow<SoilReading?>(null)
    val soilReading: StateFlow<SoilReading?> = _soilReading.asStateFlow()

    // Mock sensor state
    private val _mockSensorActive = MutableStateFlow(false)
    val mockSensorActive: StateFlow<Boolean> = _mockSensorActive.asStateFlow()
    private var mockSensorJob: Job? = null

    // ── Analysis state ────────────────────────────────────────────────────────
    private val _analysisState = MutableStateFlow<AnalysisState>(AnalysisState.Idle)
    val analysisState: StateFlow<AnalysisState> = _analysisState.asStateFlow()

    private val _streamingText = MutableStateFlow("")
    val streamingText: StateFlow<String> = _streamingText.asStateFlow()

    private val _capturedImageUri = MutableStateFlow<Uri?>(null)
    val capturedImageUri: StateFlow<Uri?> = _capturedImageUri.asStateFlow()

    private val _analysisHistory = MutableStateFlow<List<AnalysisResult>>(emptyList())
    val analysisHistory: StateFlow<List<AnalysisResult>> = _analysisHistory.asStateFlow()

    init {
        initEngine()
        // Forward real BLE readings into the merged flow
        viewModelScope.launch {
            bleManager.soilReading.collect { reading ->
                if (!_mockSensorActive.value) {
                    _soilReading.value = reading
                }
            }
        }
    }

    // ── Engine ────────────────────────────────────────────────────────────────

    private fun initEngine() {
        viewModelScope.launch { aiEngine.initialize() }
    }

    fun retryEngineLoad() = initEngine()

    // ── Mock sensor ───────────────────────────────────────────────────────────

    /**
     * Simulates a connected soil sensor with realistic, slightly-varying readings.
     * Useful for development without physical hardware.
     */
    fun enableMockSensor() {
        _mockSensorActive.value = true
        mockSensorJob?.cancel()
        mockSensorJob = viewModelScope.launch {
            // Simulate a slightly unhealthy soil profile so the AI has something to say
            var moisture    = 22f   // low — will trigger recommendation
            var ph          = 5.1f  // acidic — will trigger recommendation
            var nitrogen    = 18f   // low
            var phosphorus  = 32f   // ok
            var potassium   = 145f  // ok
            var temperature = 24f   // fine

            while (true) {
                _soilReading.value = SoilReading(
                    moisture    = moisture    + Random.nextFloat() * 1.5f - 0.75f,
                    ph          = ph          + Random.nextFloat() * 0.1f - 0.05f,
                    nitrogen    = nitrogen    + Random.nextFloat() * 2f - 1f,
                    phosphorus  = phosphorus  + Random.nextFloat() * 2f - 1f,
                    potassium   = potassium   + Random.nextFloat() * 4f - 2f,
                    temperature = temperature + Random.nextFloat() * 0.5f - 0.25f
                )
                delay(2_000) // update every 2 s, matching real sensor cadence
            }
        }
    }

    fun disableMockSensor() {
        mockSensorJob?.cancel()
        _mockSensorActive.value = false
        _soilReading.value = null
    }

    // ── Camera ────────────────────────────────────────────────────────────────

    fun onImageCaptured(uri: Uri) {
        _capturedImageUri.value = uri
        _analysisState.value = AnalysisState.Idle
        _streamingText.value = ""
    }

    fun clearImage() {
        _capturedImageUri.value = null
        _analysisState.value = AnalysisState.Idle
        _streamingText.value = ""
    }

    // ── Analysis ──────────────────────────────────────────────────────────────

    fun analyseCapture() {
        val uri = _capturedImageUri.value ?: return
        if (_analysisState.value is AnalysisState.Analyzing) return

        viewModelScope.launch {
            _analysisState.value = AnalysisState.Analyzing
            _streamingText.value = ""

            val bitmap = loadBitmapFromUri(uri)
            if (bitmap == null) {
                _analysisState.value = AnalysisState.Error("Could not load image")
                return@launch
            }

            val result = aiEngine.analyseCrop(
                imageBitmap = bitmap,
                soilReading = soilReading.value,
                onToken = { token ->
                    _streamingText.value += token
                }
            )

            result.fold(
                onSuccess = { analysis ->
                    _analysisHistory.value = listOf(analysis) + _analysisHistory.value
                    _analysisState.value = AnalysisState.Complete(analysis)
                },
                onFailure = { error ->
                    _analysisState.value = AnalysisState.Error(
                        error.message ?: "Analysis failed"
                    )
                }
            )
        }
    }

    fun analyseSoilOnly() {
        val reading = soilReading.value ?: return
        if (_analysisState.value is AnalysisState.Analyzing) return

        viewModelScope.launch {
            _analysisState.value = AnalysisState.Analyzing
            _streamingText.value = ""

            val result = aiEngine.analyseSoilOnly(
                soilReading = reading,
                onToken = { token -> _streamingText.value += token }
            )

            result.fold(
                onSuccess = { analysis ->
                    _analysisHistory.value = listOf(analysis) + _analysisHistory.value
                    _analysisState.value = AnalysisState.Complete(analysis)
                },
                onFailure = { error ->
                    _analysisState.value = AnalysisState.Error(error.message ?: "Analysis failed")
                }
            )
        }
    }

    fun resetAnalysis() {
        _analysisState.value = AnalysisState.Idle
        _streamingText.value = ""
    }

    // ── BLE ───────────────────────────────────────────────────────────────────

    fun startBleScan() = bleManager.startScan()
    fun stopBleScan() = bleManager.stopScan()
    fun connectToSensor(address: String) {
        bleManager.stopScan()
        bleManager.connect(address)
    }
    fun disconnectSensor() = bleManager.disconnect()

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun loadBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            getApplication<Application>().contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it)
            }
        } catch (e: Exception) {
            null
        }
    }

    override fun onCleared() {
        super.onCleared()
        aiEngine.release()
        bleManager.disconnect()
        mockSensorJob?.cancel()
    }
}
