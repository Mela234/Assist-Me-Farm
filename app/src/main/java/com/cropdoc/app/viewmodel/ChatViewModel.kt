package com.cropdoc.app.viewmodel

import android.Manifest
import android.app.Application
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cropdoc.app.CropDocApplication
import com.cropdoc.app.data.model.ChatMessage
import com.cropdoc.app.data.model.Crop
import com.cropdoc.app.data.model.FarmZone
import com.cropdoc.app.data.model.ModelState
import com.cropdoc.app.data.model.SoilReading
import com.cropdoc.app.data.model.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "ChatViewModel"
        private const val MAX_RECORDING_SECONDS = 30
        private const val AUDIO_SAMPLE_RATE = 16_000
    }

    private val chatRepository = CropDocApplication.instance.chatRepository
    private val farmRepository = CropDocApplication.instance.farmRepository
    private val weatherRepository = CropDocApplication.instance.weatherRepository
    private val aiEngine = CropDocApplication.instance.aiEngine

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

    private val _isMediaProcessing = MutableStateFlow(false)
    val isMediaProcessing: StateFlow<Boolean> = _isMediaProcessing.asStateFlow()

    private var messageLoadJob: Job? = null

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordingSeconds = MutableStateFlow(0)
    val recordingSeconds: StateFlow<Int> = _recordingSeconds.asStateFlow()

    private val _audioFile = MutableStateFlow<File?>(null)
    val audioFile: StateFlow<File?> = _audioFile.asStateFlow()

    private var audioRecord: AudioRecord? = null
    private var activeRecordingFile: File? = null
    private var recordingWriteJob: Job? = null
    private var recordingTimerJob: Job? = null

    private var currentSoilReading: SoilReading? = null
    private var currentWeather: WeatherData? = null

    init {
        viewModelScope.launch {
            aiEngine.initialize()
        }

        viewModelScope.launch {
            weatherRepository.latestWeather.collect { weather ->
                currentWeather = weather
            }
        }
    }

    fun openGeneralChat() {
        _currentZoneId.value = null
        _currentZone.value = null
        viewModelScope.launch {
            aiEngine.resetConversation()
            loadMessages(null)
        }
    }

    fun openZoneChat(zone: FarmZone) {
        _currentZoneId.value = zone.id
        _currentZone.value = zone
        viewModelScope.launch {
            aiEngine.resetConversation()
            loadMessages(zone.id)
        }
    }

    fun updateSoilReading(soilReading: SoilReading?) {
        currentSoilReading = soilReading
    }

    private fun loadMessages(zoneId: Long?) {
        messageLoadJob?.cancel()
        _messages.value = emptyList()

        messageLoadJob = viewModelScope.launch {
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

    fun createCameraImageUri(): Uri {
        val context = getApplication<Application>()

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "camera_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CropDoc")
        }

        return context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        ) ?: throw IllegalStateException("Could not create camera image URI")
    }

    fun deleteCameraImageUri(uri: Uri) {
        try {
            val context = getApplication<Application>()
            context.contentResolver.delete(uri, null, null)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to delete unused camera uri: $uri", e)
        }
    }

    fun attachImage(uri: Uri) {
        viewModelScope.launch {
            _isMediaProcessing.value = true

            try {
                val jpegUri = withContext(Dispatchers.IO) {
                    convertUriToJpeg(uri)
                }

                if (jpegUri != null) {
                    _attachedImageUri.value = jpegUri
                    Log.d(TAG, "Attached image ready: $jpegUri")
                } else {
                    Log.e(TAG, "Failed to attach image from uri: $uri")
                    _attachedImageUri.value = null
                }
            } finally {
                _isMediaProcessing.value = false
            }
        }
    }

    fun attachImageBitmap(bitmap: Bitmap) {
        viewModelScope.launch {
            _isMediaProcessing.value = true

            try {
                val jpegUri = withContext(Dispatchers.IO) {
                    saveCameraBitmapAsJpeg(bitmap)
                }

                if (jpegUri != null) {
                    _attachedImageUri.value = jpegUri
                    Log.d(TAG, "Attached camera bitmap image ready: $jpegUri")
                } else {
                    Log.e(TAG, "Failed to attach camera bitmap image")
                    _attachedImageUri.value = null
                }
            } finally {
                _isMediaProcessing.value = false
            }
        }
    }

    private fun convertUriToJpeg(uri: Uri): Uri? {
        return try {
            val context = getApplication<Application>()

            val bitmap = context.contentResolver.openInputStream(uri).use { input ->
                BitmapFactory.decodeStream(input)
            } ?: run {
                Log.e(TAG, "Image decode failed for uri: $uri")
                return null
            }

            saveBitmapAsJpeg(
                bitmap = bitmap,
                fileName = "attached_image_${System.currentTimeMillis()}.jpg"
            )
        } catch (e: Exception) {
            Log.e(TAG, "convertUriToJpeg failed", e)
            null
        }
    }

    private fun saveCameraBitmapAsJpeg(bitmap: Bitmap): Uri? {
        return try {
            val safeBitmap = if (bitmap.config == Bitmap.Config.ARGB_8888) {
                bitmap
            } else {
                bitmap.copy(Bitmap.Config.ARGB_8888, false)
            }

            saveBitmapAsJpeg(
                bitmap = safeBitmap,
                fileName = "camera_bitmap_${System.currentTimeMillis()}.jpg"
            )
        } catch (e: Exception) {
            Log.e(TAG, "saveCameraBitmapAsJpeg failed", e)
            null
        }
    }

    private fun saveBitmapAsJpeg(bitmap: Bitmap, fileName: String): Uri? {
        return try {
            val context = getApplication<Application>()
            val file = File(context.filesDir, fileName)

            FileOutputStream(file).use { out ->
                val success = bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                out.flush()

                if (!success) {
                    Log.e(TAG, "Bitmap compression failed")
                    return null
                }
            }

            if (!file.exists() || file.length() <= 0L) {
                Log.e(TAG, "Saved image file missing or empty")
                return null
            }

            val decoded = BitmapFactory.decodeFile(file.absolutePath)
            if (decoded == null) {
                Log.e(TAG, "Saved JPEG cannot be decoded: ${file.absolutePath}")
                return null
            }

            Log.d(TAG, "Image saved: ${file.absolutePath}, size=${file.length()}")
            Uri.fromFile(file)
        } catch (e: Exception) {
            Log.e(TAG, "saveBitmapAsJpeg failed", e)
            null
        }
    }

    fun clearAttachment() {
        if (_isMediaProcessing.value) return
        _attachedImageUri.value = null
    }

    fun startRecording() {
        if (_isRecording.value || _isMediaProcessing.value) return

        val context = getApplication<Application>()

        if (
            context.checkSelfPermission(Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(TAG, "RECORD_AUDIO permission not granted")
            return
        }

        clearAudio()

        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT

        val minBufferSize = AudioRecord.getMinBufferSize(
            AUDIO_SAMPLE_RATE,
            channelConfig,
            audioFormat
        )

        if (minBufferSize <= 0) {
            Log.e(TAG, "Invalid AudioRecord buffer size: $minBufferSize")
            return
        }

        val bufferSize = minBufferSize * 2
        val outputFile = File(context.filesDir, "voice_${System.currentTimeMillis()}.wav")

        try {
            val recorder = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                AUDIO_SAMPLE_RATE,
                channelConfig,
                audioFormat,
                bufferSize
            )

            if (recorder.state != AudioRecord.STATE_INITIALIZED) {
                Log.e(TAG, "AudioRecord failed to initialize")
                recorder.release()
                return
            }

            audioRecord = recorder
            activeRecordingFile = outputFile
            _audioFile.value = null
            _isRecording.value = true
            _recordingSeconds.value = 0

            recorder.startRecording()

            recordingWriteJob = viewModelScope.launch(Dispatchers.IO) {
                writeWavRecording(
                    recorder = recorder,
                    outputFile = outputFile,
                    bufferSize = bufferSize,
                    sampleRate = AUDIO_SAMPLE_RATE,
                    channels = 1,
                    bitsPerSample = 16
                )
            }

            recordingTimerJob = viewModelScope.launch {
                while (_recordingSeconds.value < MAX_RECORDING_SECONDS && _isRecording.value) {
                    delay(1000)
                    _recordingSeconds.value++
                }

                if (_isRecording.value) {
                    stopRecording()
                }
            }

            Log.d(TAG, "WAV recording started: ${outputFile.absolutePath}")
        } catch (e: SecurityException) {
            Log.e(TAG, "Missing RECORD_AUDIO permission", e)
            _isRecording.value = false
            releaseAudioRecord()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start WAV recording", e)
            _isRecording.value = false
            releaseAudioRecord()
        }
    }

    fun stopRecording() {
        if (!_isRecording.value) return

        recordingTimerJob?.cancel()
        _isRecording.value = false
        _isMediaProcessing.value = true

        viewModelScope.launch {
            try {
                recordingWriteJob?.join()
            } catch (e: Exception) {
                Log.e(TAG, "Recording write job failed", e)
            }

            val finishedFile = activeRecordingFile

            releaseAudioRecord()

            if (finishedFile != null && finishedFile.exists() && finishedFile.length() > 44L) {
                _audioFile.value = finishedFile
                Log.d(
                    TAG,
                    "WAV audio ready -> ${finishedFile.absolutePath}, size=${finishedFile.length()}"
                )
            } else {
                Log.e(TAG, "WAV audio file invalid or empty")
                finishedFile?.delete()
            }

            activeRecordingFile = null
            _recordingSeconds.value = 0
            _isMediaProcessing.value = false
        }
    }

    private fun writeWavRecording(
        recorder: AudioRecord,
        outputFile: File,
        bufferSize: Int,
        sampleRate: Int,
        channels: Int,
        bitsPerSample: Int
    ) {
        val buffer = ByteArray(bufferSize)

        RandomAccessFile(outputFile, "rw").use { wav ->
            writeEmptyWavHeader(
                wav = wav,
                sampleRate = sampleRate,
                channels = channels,
                bitsPerSample = bitsPerSample
            )

            var totalAudioBytes = 0L

            while (_isRecording.value) {
                val read = recorder.read(buffer, 0, buffer.size)

                if (read > 0) {
                    wav.write(buffer, 0, read)
                    totalAudioBytes += read
                }
            }

            updateWavHeader(
                wav = wav,
                totalAudioBytes = totalAudioBytes,
                sampleRate = sampleRate,
                channels = channels,
                bitsPerSample = bitsPerSample
            )

            Log.d(TAG, "WAV finalized: bytes=$totalAudioBytes")
        }
    }

    private fun writeEmptyWavHeader(
        wav: RandomAccessFile,
        sampleRate: Int,
        channels: Int,
        bitsPerSample: Int
    ) {
        wav.setLength(0)

        wav.writeBytes("RIFF")
        writeIntLE(wav, 0)
        wav.writeBytes("WAVE")

        wav.writeBytes("fmt ")
        writeIntLE(wav, 16)
        writeShortLE(wav, 1)
        writeShortLE(wav, channels)
        writeIntLE(wav, sampleRate)
        writeIntLE(wav, sampleRate * channels * bitsPerSample / 8)
        writeShortLE(wav, channels * bitsPerSample / 8)
        writeShortLE(wav, bitsPerSample)

        wav.writeBytes("data")
        writeIntLE(wav, 0)
    }

    private fun updateWavHeader(
        wav: RandomAccessFile,
        totalAudioBytes: Long,
        sampleRate: Int,
        channels: Int,
        bitsPerSample: Int
    ) {
        wav.seek(4)
        writeIntLE(wav, (36 + totalAudioBytes).toInt())

        wav.seek(24)
        writeIntLE(wav, sampleRate)

        wav.seek(28)
        writeIntLE(wav, sampleRate * channels * bitsPerSample / 8)

        wav.seek(32)
        writeShortLE(wav, channels * bitsPerSample / 8)

        wav.seek(40)
        writeIntLE(wav, totalAudioBytes.toInt())
    }

    private fun writeIntLE(file: RandomAccessFile, value: Int) {
        file.write(value and 0xff)
        file.write((value shr 8) and 0xff)
        file.write((value shr 16) and 0xff)
        file.write((value shr 24) and 0xff)
    }

    private fun writeShortLE(file: RandomAccessFile, value: Int) {
        file.write(value and 0xff)
        file.write((value shr 8) and 0xff)
    }

    private fun releaseAudioRecord() {
        try {
            audioRecord?.stop()
        } catch (_: Exception) {}

        try {
            audioRecord?.release()
        } catch (_: Exception) {}

        audioRecord = null
    }

    fun clearAudio() {
        if (_isMediaProcessing.value) return

        if (_isRecording.value) {
            stopRecording()
            return
        }

        _audioFile.value?.delete()
        _audioFile.value = null
        _recordingSeconds.value = 0
    }

    private fun copyFileForModel(source: File, prefix: String): File {
        if (!source.exists() || source.length() <= 0L) {
            throw IllegalStateException("Media file invalid: ${source.absolutePath}")
        }

        val extension = source.extension.ifBlank {
            if (prefix.contains("audio")) "wav" else "jpg"
        }

        val stableFile = File(
            getApplication<Application>().cacheDir,
            "${prefix}_${System.currentTimeMillis()}.$extension"
        )

        source.copyTo(stableFile, overwrite = true)

        if (!stableFile.exists() || stableFile.length() <= 0L) {
            throw IllegalStateException("Stable media copy failed: ${stableFile.absolutePath}")
        }

        Log.d(
            TAG,
            "Stable media copy -> source=${source.absolutePath}, stable=${stableFile.absolutePath}, size=${stableFile.length()}"
        )

        return stableFile
    }

    private fun uriToStableImageUri(uri: Uri): Uri {
        val context = getApplication<Application>()

        val sourceFile = if (uri.scheme == "file") {
            File(uri.path ?: "")
        } else {
            val temp = File(
                context.cacheDir,
                "stable_image_source_${System.currentTimeMillis()}.jpg"
            )

            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(temp).use { output ->
                    input.copyTo(output)
                    output.flush()
                }
            } ?: throw IllegalStateException("Could not read image uri: $uri")

            temp
        }

        val stableFile = copyFileForModel(sourceFile, "model_image")
        return Uri.fromFile(stableFile)
    }

    fun sendMessage(text: String): Boolean {
        if (_isMediaProcessing.value) {
            Log.w(TAG, "Blocked send: media is still processing")
            return false
        }

        if (text.isBlank() && _attachedImageUri.value == null && _audioFile.value == null) {
            return false
        }

        if (_isTyping.value) return false

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
            return false
        }

        val rawImageUri = _attachedImageUri.value
        val rawAudio = _audioFile.value

        val imageUri = try {
            rawImageUri?.let { uriToStableImageUri(it) }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to prepare stable image file", e)
            null
        }

        val audio = try {
            rawAudio?.let { copyFileForModel(it, "model_audio") }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to prepare stable audio file", e)
            null
        }

        Log.d(
            TAG,
            "FINAL SEND -> text=${text.isNotBlank()}, imageUri=$imageUri, audio=${audio?.absolutePath}, audioExists=${audio?.exists()}, audioSize=${audio?.length()}"
        )

        _attachedImageUri.value = null
        _audioFile.value = null

        viewModelScope.launch {
            val messageContent = when {
                text.isNotBlank() -> text
                audio != null -> "🎤 Voice message"
                imageUri != null -> "📷 Image"
                else -> ""
            }

            val engineText = buildString {
                if (imageUri != null) {
                    append("Analyze the attached image. ")
                }

                if (audio != null) {
                    append(
                        "The farmer sent a voice message. Listen to the audio directly, understand what language they used, transcribe it if helpful, then respond naturally in the requested app language. "
                    )
                }

                if (text.isNotBlank()) {
                    append(text)
                }

                if (isBlank()) {
                    append("Please respond to the farmer's message.")
                }
            }.trim()

            val userMessage = ChatMessage(
                role = "USER",
                content = messageContent,
                attachedImageUri = imageUri?.toString(),
                audioPath = audio?.absolutePath,
                zoneId = _currentZoneId.value,
                contextSnapshot = buildContextSnapshot()
            )

            chatRepository.sendMessage(userMessage)

            _isTyping.value = true
            _streamingText.value = ""

            val recentHistory = chatRepository.getRecentMessages(_currentZoneId.value, 6)
            val zone = _currentZone.value

            val crop: Crop?
            val allZonesWithCrops: List<Pair<FarmZone, Crop?>>

            if (zone != null) {
                crop = farmRepository.getLatestCropForZone(zone.id)
                allZonesWithCrops = emptyList()
            } else {
                crop = null
                val zones = farmRepository.allZones.first()
                allZonesWithCrops = zones.map { z ->
                    z to farmRepository.getLatestCropForZone(z.id)
                }
            }

            Log.d(
                TAG,
                "MODEL MEDIA CHECK -> imageUri=$imageUri, audio=${audio?.absolutePath}, audioExists=${audio?.exists()}, audioSize=${audio?.length()}"
            )

            val result = aiEngine.chat(
                userMessage = engineText,
                imageUri = imageUri,
                audioFile = audio,
                soilReading = currentSoilReading,
                weatherData = currentWeather,
                zone = zone,
                crop = crop,
                allZonesWithCrops = allZonesWithCrops,
                history = recentHistory,
                context = getApplication(),
                onToken = { token ->
                    _streamingText.value += token
                }
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

        return true
    }

    suspend fun setLanguage(code: String) {
        aiEngine.setLanguage(code)
    }

    fun clearChat() {
        viewModelScope.launch {
            val zoneId = _currentZoneId.value

            if (zoneId == null) {
                chatRepository.clearGeneralChat()
            } else {
                chatRepository.clearZoneChat(zoneId)
            }

            aiEngine.resetConversation()
        }
    }

    private suspend fun buildContextSnapshot(): String {
        return try {
            val json = JSONObject()

            currentSoilReading?.let { soil ->
                json.put(
                    "soil",
                    JSONObject().apply {
                        put("moisture", soil.moisture)
                        put("ph", soil.ph)
                        put("nitrogen", soil.nitrogen)
                        put("phosphorus", soil.phosphorus)
                        put("potassium", soil.potassium)
                        put("temperature", soil.temperature)
                    }
                )
            }

            currentWeather?.let { weather ->
                json.put(
                    "weather",
                    JSONObject().apply {
                        put("temperature", weather.temperature)
                        put("humidity", weather.humidity)
                        put("rainfall", weather.rainfall)
                        put("forecast", weather.forecast)
                    }
                )
            }

            _currentZone.value?.let { zone ->
                json.put(
                    "zone",
                    JSONObject().apply {
                        put("name", zone.name)

                        val crop = farmRepository.getLatestCropForZone(zone.id)
                        crop?.let {
                            put("crop", it.name)
                            put("plantedDate", it.plantedDate)
                            put("expectedHarvestDays", it.expectedHarvestDays)

                            val daysPlanted =
                                ((System.currentTimeMillis() - it.plantedDate) /
                                        (1000 * 60 * 60 * 24)).toInt()

                            put("daysPlanted", daysPlanted)
                            put(
                                "daysToHarvest",
                                (it.expectedHarvestDays - daysPlanted).coerceAtLeast(0)
                            )
                        }
                    }
                )
            }

            json.toString()
        } catch (e: Exception) {
            ""
        }
    }

    override fun onCleared() {
        super.onCleared()
        messageLoadJob?.cancel()
        recordingTimerJob?.cancel()
        recordingWriteJob?.cancel()
        releaseAudioRecord()
        aiEngine.release()
    }
}