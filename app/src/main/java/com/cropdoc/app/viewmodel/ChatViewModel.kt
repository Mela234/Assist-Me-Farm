package com.cropdoc.app.viewmodel

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cropdoc.app.CropDocApplication
import com.cropdoc.app.data.model.ChatMessage
import com.cropdoc.app.data.model.FarmZone
import com.cropdoc.app.data.model.SoilReading
import com.cropdoc.app.data.model.WeatherData
import com.cropdoc.app.data.model.ModelState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder

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

    // WAV recording constants
    private val SAMPLE_RATE    = 16000
    private val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    private val AUDIO_FORMAT   = AudioFormat.ENCODING_PCM_16BIT

    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null
    private var recordingTimerJob: Job? = null
    private var currentWavFile: File? = null

    private var currentSoilReading: SoilReading? = null
    private var currentWeather: WeatherData? = null

    init {
        viewModelScope.launch { aiEngine.initialize() }
        viewModelScope.launch {
            weatherRepository.latestWeather.collect { weather -> currentWeather = weather }
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
                chatRepository.getGeneralChat().collect { msgs -> _messages.value = msgs }
            } else {
                chatRepository.getChatForZone(zoneId).collect { msgs -> _messages.value = msgs }
            }
        }
    }

    // ── Image attachment ──────────────────────────────────────────────────────

    fun attachImage(uri: Uri) {
        viewModelScope.launch {
            val permanentUri = withContext(Dispatchers.IO) { copyUriToInternal(uri) }
            _attachedImageUri.value = permanentUri ?: uri
        }
    }

    private fun copyUriToInternal(uri: Uri): Uri? {
        return try {
            val context = getApplication<Application>()
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val file = File(context.filesDir, "img_${System.currentTimeMillis()}.jpg")
            file.outputStream().use { inputStream.copyTo(it) }
            Uri.fromFile(file)
        } catch (e: Exception) { null }
    }

    fun clearAttachment() { _attachedImageUri.value = null }

    // ── Audio recording (WAV via AudioRecord) ─────────────────────────────────

    fun startRecording() {
        if (_isRecording.value) return

        val context = getApplication<Application>()

        // Explicit permission check — satisfies lint and guards against revoked permission
        if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            _isRecording.value = false
            return
        }

        val bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
            .coerceAtLeast(8192)

        try {
            val wavFile = File(context.filesDir, "audio_${System.currentTimeMillis()}.wav")
            currentWavFile = wavFile

            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSize
            ).also { it.startRecording() }

            _isRecording.value = true
            _recordingSeconds.value = 0

            // Capture PCM on IO thread and stream to file
            recordingJob = viewModelScope.launch(Dispatchers.IO) {
                val fos = FileOutputStream(wavFile)

                // Placeholder 44-byte WAV header — filled in correctly after recording stops
                fos.write(ByteArray(44))

                val buffer = ByteArray(bufferSize)
                var totalPcmBytes = 0L

                while (_isRecording.value) {
                    val read = audioRecord?.read(buffer, 0, buffer.size) ?: break
                    if (read > 0) {
                        fos.write(buffer, 0, read)
                        totalPcmBytes += read
                    }
                }

                fos.flush()
                fos.close()

                // Rewrite correct header now that we know total data size
                writeWavHeader(wavFile, totalPcmBytes)
            }

            // Auto-stop timer at 30 s
            recordingTimerJob = viewModelScope.launch {
                while (_recordingSeconds.value < 30) {
                    delay(1000)
                    _recordingSeconds.value++
                }
                stopRecording()
            }

        } catch (e: Exception) {
            _isRecording.value = false
            audioRecord?.release()
            audioRecord = null
        }
    }

    fun stopRecording() {
        if (!_isRecording.value) return

        _isRecording.value = false   // signals the recordingJob loop to exit
        recordingTimerJob?.cancel()

        try { audioRecord?.stop() } catch (_: Exception) {}
        audioRecord?.release()
        audioRecord = null

        // Wait for IO job to finish writing before exposing the file
        viewModelScope.launch {
            recordingJob?.join()
            val file = currentWavFile
            if (file != null && file.exists() && file.length() > 44) {
                _audioFile.value = file
            }
            currentWavFile = null
        }
    }

    fun clearAudio() {
        _audioFile.value?.delete()
        _audioFile.value = null
        _recordingSeconds.value = 0
        currentWavFile = null
    }

    /**
     * Writes a standard 16-bit mono PCM WAV header at the beginning of [file].
     * [pcmDataBytes] is the number of raw PCM bytes that follow the 44-byte header.
     */
    private fun writeWavHeader(file: File, pcmDataBytes: Long) {
        val channels      = 1
        val bitsPerSample = 16
        val byteRate      = SAMPLE_RATE * channels * bitsPerSample / 8
        val blockAlign    = channels * bitsPerSample / 8

        val header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN).apply {
            put("RIFF".toByteArray())
            putInt((36 + pcmDataBytes).toInt())   // RIFF chunk size
            put("WAVE".toByteArray())
            put("fmt ".toByteArray())
            putInt(16)                             // fmt chunk size
            putShort(1)                            // PCM format
            putShort(channels.toShort())
            putInt(SAMPLE_RATE)
            putInt(byteRate)
            putShort(blockAlign.toShort())
            putShort(bitsPerSample.toShort())
            put("data".toByteArray())
            putInt(pcmDataBytes.toInt())           // data chunk size
        }.array()

        RandomAccessFile(file, "rw").use { raf ->
            raf.seek(0)
            raf.write(header)
        }
    }

    // ── Sending messages ──────────────────────────────────────────────────────

    fun sendMessage(text: String) {
        if (text.isBlank() && _attachedImageUri.value == null && _audioFile.value == null) return
        if (_isTyping.value) return

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
        val audio    = _audioFile.value
        _attachedImageUri.value = null
        _audioFile.value = null

        viewModelScope.launch {
            val userMessage = ChatMessage(
                role = "USER",
                content = text.ifBlank { "🎤 Voice message" },
                attachedImageUri = imageUri?.toString(),
                audioPath = audio?.absolutePath,
                zoneId = _currentZoneId.value,
                contextSnapshot = buildContextSnapshot()
            )
            chatRepository.sendMessage(userMessage)

            _isTyping.value = true
            _streamingText.value = ""

            val recentHistory = chatRepository.getRecentMessages(_currentZoneId.value, 10)
            val crop = _currentZone.value?.let { farmRepository.getLatestCropForZone(it.id) }

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
        } catch (e: Exception) { "" }
    }

    override fun onCleared() {
        super.onCleared()
        aiEngine.release()
        try { audioRecord?.stop() } catch (_: Exception) {}
        audioRecord?.release()
        recordingJob?.cancel()
        recordingTimerJob?.cancel()
    }
}