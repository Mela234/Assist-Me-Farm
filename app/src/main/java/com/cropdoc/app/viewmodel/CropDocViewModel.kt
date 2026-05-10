package com.cropdoc.app.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cropdoc.app.CropDocApplication
import com.cropdoc.app.LANGUAGE_KEY
import com.cropdoc.app.dataStore
import com.cropdoc.app.data.ble.SoilSensorBleManager
import com.cropdoc.app.data.model.AnalysisResult
import com.cropdoc.app.data.model.AnalysisState
import com.cropdoc.app.data.model.BleState
import com.cropdoc.app.data.model.CropDocAiEngine
import com.cropdoc.app.data.model.FarmZone
import com.cropdoc.app.data.model.ModelState
import com.cropdoc.app.data.model.SoilReading
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.random.Random

class CropDocViewModel(application: Application) : AndroidViewModel(application) {

    private val aiEngine = CropDocAiEngine(application)
    val bleManager = SoilSensorBleManager(application)

    private val farmRepository = CropDocApplication.instance.farmRepository
    private val weatherRepository = CropDocApplication.instance.weatherRepository

    // ── Model state ───────────────────────────────────────────────────────────
    val modelState: StateFlow<ModelState> = aiEngine.modelState

    // ── BLE state ─────────────────────────────────────────────────────────────
    val bleState: StateFlow<BleState> = bleManager.bleState
    val scannedDevices = bleManager.scannedDevices

    // ── Soil reading ──────────────────────────────────────────────────────────
    private val _soilReading = MutableStateFlow<SoilReading?>(null)
    val soilReading: StateFlow<SoilReading?> = _soilReading.asStateFlow()

    // ── Mock sensor ───────────────────────────────────────────────────────────
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

    // ── Soil summary ──────────────────────────────────────────────────────────
    private val _soilSummary = MutableStateFlow<String?>(null)
    val soilSummary: StateFlow<String?> = _soilSummary.asStateFlow()

    private val _soilSummaryLoading = MutableStateFlow(false)
    val soilSummaryLoading: StateFlow<Boolean> = _soilSummaryLoading.asStateFlow()

    // ── Language ──────────────────────────────────────────────────────────────
    private val _currentLanguage = MutableStateFlow("en")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    // ── Weather ───────────────────────────────────────────────────────────────
    val latestWeather = weatherRepository.latestWeather
    val weatherProfile = weatherRepository.weatherProfile

    // ── Farm ──────────────────────────────────────────────────────────────────
    val allZones = farmRepository.allZones

    private val _activeZoneState = MutableStateFlow<FarmZone?>(null)
    val activeZone: StateFlow<FarmZone?> = _activeZoneState.asStateFlow()

    init {
        initEngine()

        // Mirror active zone from DB into StateFlow
        viewModelScope.launch {
            farmRepository.activeZone.collect { zone ->
                _activeZoneState.value = zone
            }
        }

        // Forward real BLE readings into merged flow
        viewModelScope.launch {
            bleManager.soilReading.collect { reading ->
                if (!_mockSensorActive.value) {
                    _soilReading.value = reading
                    reading?.let { r ->
                        _activeZoneState.value?.let { zone ->
                            farmRepository.saveReading(zone.id, r)
                        }
                    }
                }
            }
        }

        // Observe language
        viewModelScope.launch {
            application.dataStore.data.map { prefs ->
                prefs[LANGUAGE_KEY] ?: "en"
            }.collect { code ->
                _currentLanguage.value = code
                aiEngine.setLanguage(code)
            }
        }
    }

    // ── Engine ────────────────────────────────────────────────────────────────

    private fun initEngine() {
        viewModelScope.launch { aiEngine.initialize() }
    }

    fun retryEngineLoad() = initEngine()

    // ── Mock sensor ───────────────────────────────────────────────────────────

    fun enableMockSensor() {
        _mockSensorActive.value = true
        mockSensorJob?.cancel()
        mockSensorJob = viewModelScope.launch {
            var moisture    = 22f
            var ph          = 5.1f
            var nitrogen    = 18f
            var phosphorus  = 32f
            var potassium   = 145f
            var temperature = 24f

            while (true) {
                val reading = SoilReading(
                    moisture    = moisture    + Random.nextFloat() * 1.5f - 0.75f,
                    ph          = ph          + Random.nextFloat() * 0.1f - 0.05f,
                    nitrogen    = nitrogen    + Random.nextFloat() * 2f - 1f,
                    phosphorus  = phosphorus  + Random.nextFloat() * 2f - 1f,
                    potassium   = potassium   + Random.nextFloat() * 4f - 2f,
                    temperature = temperature + Random.nextFloat() * 0.5f - 0.25f
                )
                _soilReading.value = reading
                _activeZoneState.value?.let { zone ->
                    farmRepository.saveReading(zone.id, reading)
                }
                delay(2_000)
            }
        }
    }

    fun disableMockSensor() {
        mockSensorJob?.cancel()
        _mockSensorActive.value = false
        _soilReading.value = null
        _soilSummary.value = null
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

    fun analyseCapture(includeSoil: Boolean = true) {
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
                soilReading = if (includeSoil) soilReading.value else null,
                onToken = { token -> _streamingText.value += token }
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
                    _analysisState.value = AnalysisState.Error(
                        error.message ?: "Analysis failed"
                    )
                }
            )
        }
    }

    // ── Soil summary ──────────────────────────────────────────────────────────

    fun summariseSoil() {
        val reading = soilReading.value ?: return
        if (_soilSummaryLoading.value) return

        viewModelScope.launch {
            _soilSummaryLoading.value = true
            _soilSummary.value = null
            _soilSummary.value = aiEngine.summariseSoil(reading)
            _soilSummaryLoading.value = false
        }
    }

    fun clearSoilSummary() {
        _soilSummary.value = null
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

    // ── Language ──────────────────────────────────────────────────────────────

    fun setLanguage(code: String) {
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { prefs ->
                prefs[LANGUAGE_KEY] = code
            }
            _currentLanguage.value = code
            aiEngine.setLanguage(code)
        }
    }

    // ── Farm zone helpers ─────────────────────────────────────────────────────

    fun setActiveZone(zoneId: Long) {
        viewModelScope.launch {
            farmRepository.setActiveZone(zoneId)
        }
    }

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