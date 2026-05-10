package com.cropdoc.app.viewmodel

import android.app.Application
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cropdoc.app.CropDocApplication
import com.cropdoc.app.data.model.ChatMessage
import com.cropdoc.app.data.model.CropDocAiEngine
import com.cropdoc.app.data.model.FarmZone
import com.cropdoc.app.data.model.SoilReading
import com.cropdoc.app.data.model.WeatherData
import com.cropdoc.app.data.model.ModelState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val chatRepository = CropDocApplication.instance.chatRepository
    private val farmRepository = CropDocApplication.instance.farmRepository
    private val weatherRepository = CropDocApplication.instance.weatherRepository

    private val aiEngine = CropDocApplication.instance.aiEngine

    // ── Chat state ────────────────────────────────────────────────────────────

    private val _currentZoneId = MutableStateFlow<Long?>(null)
    val currentZoneId: StateFlow<Long?> = _currentZoneId.asStateFlow()

    private val _currentZone = MutableStateFlow<FarmZone?>(null)
    val currentZone: StateFlow<FarmZone?> = _currentZone.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    private val _streamingText = MutableStateFlow("")
    val streamingText: StateFlow<String> = _streamingText.asStateFlow()

    private val _attachedImageUri = MutableStateFlow<Uri?>(null)
    val attachedImageUri: StateFlow<Uri?> = _attachedImageUri.asStateFlow()

    // ── Audio recording state ─────────────────────────────────────────────────

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordingSeconds = MutableStateFlow(0)
    val recordingSeconds: StateFlow<Int> = _recordingSeconds.asStateFlow()

    private val _audioFile = MutableStateFlow<File?>(null)
    val audioFile: StateFlow<File?> = _audioFile.asStateFlow()

    private var mediaRecorder: MediaRecorder? = null
    private var recordingTimerJob: Job? = null
    private var currentAudioPath: String? = null

    // Current context
    private var currentSoilReading: SoilReading? = null
    private var currentWeather: WeatherData? = null

    init {
        viewModelScope.launch { aiEngine.initialize() }

        viewModelScope.launch {
            weatherRepository.latestWeather.collect { weather ->
                currentWeather = weather
            }
        }
    }

    // ── Setup ─────────────────────────────────────────────────────────────────

    fun openGeneralChat() {
        _currentZoneId.value = null
        _currentZone.value = null
        loadMessages(null)
    }

    fun openZoneChat(zone: FarmZone) {
        _currentZoneId.value = zone.id
        _currentZone.value = zone
        loadMessages(zone.id)
    }

    fun updateSoilReading(soilReading: SoilReading?) {
        currentSoilReading = soilReading
    }

    private fun loadMessages(zoneId: Long?) {
        viewModelScope.launch {
            if (zoneId == null) {
                chatRepository.getGeneralChat().collect { msgs ->
                    _messages.value = msgs
                }
            } else {
                chatRepository.getChatForZone(zoneId).collect { msgs ->
                    _messages.value = msgs
                }
            }
        }
    }

    // ── Image attachment ──────────────────────────────────────────────────────

    fun attachImage(uri: Uri) {
        _attachedImageUri.value = uri
    }

    fun clearAttachment() {
        _attachedImageUri.value = null
    }

    // ── Audio recording ───────────────────────────────────────────────────────

    fun startRecording() {
        if (_isRecording.value) return

        try {
            val audioDir = getApplication<Application>().cacheDir
            val file = File(audioDir, "chat_audio_${System.currentTimeMillis()}.m4a")
            currentAudioPath = file.absolutePath

            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(getApplication())
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioSamplingRate(16000)
                setAudioEncodingBitRate(128000)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }

            _isRecording.value = true
            _recordingSeconds.value = 0

            // Timer — auto stop at 30 seconds
            recordingTimerJob = viewModelScope.launch {
                while (_recordingSeconds.value < 30) {
                    delay(1000)
                    _recordingSeconds.value++
                }
                stopRecording()
            }

        } catch (e: Exception) {
            _isRecording.value = false
            mediaRecorder?.release()
            mediaRecorder = null
        }
    }

    fun stopRecording() {
        if (!_isRecording.value) return

        recordingTimerJob?.cancel()

        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            // ignore stop errors
        }

        mediaRecorder = null
        _isRecording.value = false

        currentAudioPath?.let { path ->
            val file = File(path)
            if (file.exists() && file.length() > 0) {
                _audioFile.value = file
            }
        }
    }

    fun clearAudio() {
        _audioFile.value?.delete()
        _audioFile.value = null
        _recordingSeconds.value = 0
        currentAudioPath = null
    }

    // ── Sending messages ──────────────────────────────────────────────────────

    fun sendMessage(text: String) {
        if (text.isBlank() && _attachedImageUri.value == null && _audioFile.value == null) return
        if (_isTyping.value) return

        // Wait for engine to be ready
        if (aiEngine.modelState.value !is ModelState.Ready) {
            viewModelScope.launch {
                chatRepository.sendMessage(
                    ChatMessage(
                        role = "ASSISTANT",
                        content = "AI model is still loading, please wait a moment and try again.",
                        zoneId = _currentZoneId.value
                    )
                )
            }
            return
        }

        val imageUri = _attachedImageUri.value
        val audio = _audioFile.value
        _attachedImageUri.value = null
        _audioFile.value = null

        viewModelScope.launch {
            val userMessage = ChatMessage(
                role = "USER",
                content = text.ifBlank { "🎤 Voice message" },
                attachedImageUri = imageUri?.toString(),
                zoneId = _currentZoneId.value,
                contextSnapshot = buildContextSnapshot()
            )
            chatRepository.sendMessage(userMessage)

            _isTyping.value = true
            _streamingText.value = ""

            val recentHistory = chatRepository.getRecentMessages(_currentZoneId.value, 10)

            val crop = _currentZone.value?.let {
                farmRepository.getLatestCropForZone(it.id)
            }

            val result = aiEngine.chat(
                userMessage = text.ifBlank { "Please respond to my voice message" },
                imageUri = imageUri,
                audioFile = audio,
                soilReading = currentSoilReading,
                weatherData = currentWeather,
                zone = _currentZone.value,
                crop = crop,
                history = recentHistory,
                context = getApplication(),
                onToken = { token -> _streamingText.value += token }
            )

            result.fold(
                onSuccess = { response ->
                    chatRepository.sendMessage(
                        ChatMessage(
                            role = "ASSISTANT",
                            content = response,
                            zoneId = _currentZoneId.value,
                            contextSnapshot = buildContextSnapshot()
                        )
                    )
                    audio?.delete()
                },
                onFailure = { error ->
                    chatRepository.sendMessage(
                        ChatMessage(
                            role = "ASSISTANT",
                            content = "Sorry, I encountered an error: ${error.message}",
                            zoneId = _currentZoneId.value
                        )
                    )
                }
            )

            _isTyping.value = false
            _streamingText.value = ""
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            val zoneId = _currentZoneId.value
            if (zoneId == null) chatRepository.clearGeneralChat()
            else chatRepository.clearZoneChat(zoneId)
        }
    }

    // ── Context builder ───────────────────────────────────────────────────────

    private suspend fun buildContextSnapshot(): String {
        return try {
            val json = JSONObject()
            currentSoilReading?.let { soil ->
                json.put("soil", JSONObject().apply {
                    put("moisture", soil.moisture)
                    put("ph", soil.ph)
                    put("nitrogen", soil.nitrogen)
                    put("phosphorus", soil.phosphorus)
                    put("potassium", soil.potassium)
                    put("temperature", soil.temperature)
                })
            }
            currentWeather?.let { weather ->
                json.put("weather", JSONObject().apply {
                    put("temperature", weather.temperature)
                    put("humidity", weather.humidity)
                    put("rainfall", weather.rainfall)
                    put("forecast", weather.forecast)
                })
            }
            _currentZone.value?.let { zone ->
                json.put("zone", JSONObject().apply {
                    put("name", zone.name)
                    val crop = farmRepository.getLatestCropForZone(zone.id)
                    crop?.let {
                        put("crop", it.name)
                        put("plantedDate", it.plantedDate)
                        put("expectedHarvestDays", it.expectedHarvestDays)
                        val daysPlanted = ((System.currentTimeMillis() - it.plantedDate) / (1000 * 60 * 60 * 24)).toInt()
                        put("daysPlanted", daysPlanted)
                        put("daysToHarvest", (it.expectedHarvestDays - daysPlanted).coerceAtLeast(0))
                    }
                })
            }
            json.toString()
        } catch (e: Exception) {
            ""
        }
    }

    override fun onCleared() {
        super.onCleared()
        aiEngine.release()
        mediaRecorder?.release()
        recordingTimerJob?.cancel()
    }
}