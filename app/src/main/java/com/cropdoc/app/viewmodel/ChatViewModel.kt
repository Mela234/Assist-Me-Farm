package com.cropdoc.app.viewmodel

import android.Manifest
import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
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

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "ChatViewModel"
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

    // _audioFile is kept for UI compatibility — ChatScreen checks audioFile != null to show
    // the "Voice message ready" card and enable the send button. It points to a small sentinel
    // file written when transcription completes. The actual transcript lives in _pendingTranscript.
    private val _audioFile = MutableStateFlow<File?>(null)
    val audioFile: StateFlow<File?> = _audioFile.asStateFlow()

    // Accumulated transcript across all recognition restarts
    private val _pendingTranscript = MutableStateFlow<String?>(null)

    private var speechRecognizer: SpeechRecognizer? = null
    private var recognizerTimerJob: Job? = null

    // Chunks of recognised text accumulated across continuous restarts
    private val transcriptChunks = mutableListOf<String>()

    // The last recognizer intent — stored so we can restart without rebuilding it
    private var recognizerIntent: Intent? = null

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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CropDoc")
            }
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

    // -----------------------------------------------------------------------------------------
    // Recording — SpeechRecognizer with two fixes applied:
    //
    // Fix 1 — Beep suppression
    //   Android plays a system sound on every startListening() call. We mute STREAM_MUSIC
    //   for 300ms before each call, then restore the original volume. This covers the beep
    //   without affecting the farmer's audio capture since the mic is not on that stream.
    //
    // Fix 2 — Continuous recognition (no premature cutoff)
    //   SpeechRecognizer is designed for short commands and stops on ~3s of silence.
    //   We work around this by restarting the recognizer automatically whenever it fires
    //   onResults or a recoverable onError (timeout / no match) while _isRecording is
    //   still true. Each result chunk is appended to transcriptChunks. When the farmer
    //   taps Stop, stopListening() is called, the final onResults fires, and all chunks
    //   are joined into the complete transcript.
    // -----------------------------------------------------------------------------------------

    fun startRecording() {
        if (_isRecording.value || _isMediaProcessing.value) return

        val context = getApplication<Application>()

        if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(TAG, "RECORD_AUDIO permission not granted")
            return
        }

        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Log.e(TAG, "Speech recognition not available on this device")
            return
        }

        _isRecording.value = true
        _recordingSeconds.value = 0
        _pendingTranscript.value = null
        _audioFile.value = null
        transcriptChunks.clear()

        recognizerIntent = buildRecognizerIntent()

        viewModelScope.launch(Dispatchers.Main) {
            createAndStartRecognizer(context)
        }

        // Timer drives the progress bar in ChatScreen — same as before
        recognizerTimerJob = viewModelScope.launch {
            while (_recordingSeconds.value < 30 && _isRecording.value) {
                delay(1000)
                _recordingSeconds.value++
            }
            if (_isRecording.value) stopRecording()
        }
    }

    private fun buildRecognizerIntent(): Intent =
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            // Keep listening for up to 10s of silence before auto-firing onResults
            putExtra(
                RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
                10_000L
            )
            putExtra(
                RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
                8_000L
            )
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 500L)
        }

    // Must be called on the main thread. Creates a fresh SpeechRecognizer, suppresses
    // the system beep, then starts listening.
    private fun createAndStartRecognizer(context: Context) {
        destroyRecognizer()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer?.setRecognitionListener(recognitionListener)

        // Fix 1: mute STREAM_MUSIC for 300ms to suppress the start beep
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val prevVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)

        speechRecognizer?.startListening(recognizerIntent)

        viewModelScope.launch(Dispatchers.Main) {
            delay(300)
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, prevVolume, 0)
        }
    }

    private val recognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {
            Log.d(TAG, "SpeechRecognizer: ready")
        }

        override fun onBeginningOfSpeech() {
            Log.d(TAG, "SpeechRecognizer: speech detected")
        }

        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}

        override fun onEndOfSpeech() {
            Log.d(TAG, "SpeechRecognizer: end of speech, processing…")
        }

        override fun onPartialResults(partialResults: Bundle?) {
            // Partial results are just for logging — we accumulate final chunks only
            val partial = partialResults
                ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                ?.firstOrNull()
            if (!partial.isNullOrBlank()) {
                Log.d(TAG, "Partial: $partial")
            }
        }

        override fun onResults(results: Bundle?) {
            val chunk = results
                ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                ?.firstOrNull()
                ?.trim()

            if (!chunk.isNullOrBlank()) {
                Log.d(TAG, "Chunk recognised: $chunk")
                transcriptChunks.add(chunk)
            }

            if (_isRecording.value) {
                // Fix 2: still recording — restart to keep capturing without cutoff
                Log.d(TAG, "Restarting recognizer for continuous capture")
                val context = getApplication<Application>()
                viewModelScope.launch(Dispatchers.Main) {
                    createAndStartRecognizer(context)
                }
            } else {
                // Farmer tapped Stop — finalize the transcript
                finalizeTranscript()
            }
        }

        override fun onError(error: Int) {
            Log.w(TAG, "SpeechRecognizer error: $error")

            val isRecoverable = error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT ||
                    error == SpeechRecognizer.ERROR_NO_MATCH ||
                    error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY

            if (_isRecording.value && isRecoverable) {
                // Fix 2: timeout / no match while still recording — restart silently
                Log.d(TAG, "Recoverable error while recording, restarting recognizer")
                val context = getApplication<Application>()
                viewModelScope.launch(Dispatchers.Main) {
                    createAndStartRecognizer(context)
                }
            } else {
                // Non-recoverable or farmer has already stopped — finalize
                finalizeTranscript()
            }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {}
    }

    private fun finalizeTranscript() {
        recognizerTimerJob?.cancel()
        destroyRecognizer()

        val fullTranscript = transcriptChunks.joinToString(" ").trim()

        if (fullTranscript.isNotBlank()) {
            Log.d(TAG, "Final transcript: $fullTranscript")
            _pendingTranscript.value = fullTranscript

            // Write a sentinel file so the ChatScreen UI shows "Voice message ready"
            val context = getApplication<Application>()
            try {
                val sentinelFile = File(
                    context.filesDir,
                    "voice_${System.currentTimeMillis()}.txt"
                )
                sentinelFile.writeText(fullTranscript)
                _audioFile.value = sentinelFile
            } catch (e: Exception) {
                Log.w(TAG, "Sentinel file write failed", e)
                _audioFile.value = File(context.filesDir, "voice_ready.txt")
            }
        } else {
            Log.w(TAG, "No transcript captured")
        }

        _isRecording.value = false
        _isMediaProcessing.value = false
        transcriptChunks.clear()
    }

    fun stopRecording() {
        if (!_isRecording.value) return

        recognizerTimerJob?.cancel()
        _isRecording.value = false       // signals onResults NOT to restart
        _isMediaProcessing.value = true  // show spinner while final chunk is processed

        // stopListening() causes onResults to fire one last time with whatever was captured,
        // then finalizeTranscript() is called from inside onResults
        viewModelScope.launch(Dispatchers.Main) {
            speechRecognizer?.stopListening()
        }
    }

    private fun destroyRecognizer() {
        try {
            speechRecognizer?.destroy()
        } catch (_: Exception) {}
        speechRecognizer = null
    }

    fun clearAudio() {
        if (_isMediaProcessing.value) return
        _audioFile.value?.delete()
        _audioFile.value = null
        _pendingTranscript.value = null
        _recordingSeconds.value = 0
        transcriptChunks.clear()
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

        val imageUri = _attachedImageUri.value
        val audio = _audioFile.value
        val transcript = _pendingTranscript.value

        Log.d(
            TAG,
            "FINAL SEND -> text=${text.isNotBlank()}, imageUri=$imageUri, transcript=$transcript"
        )

        _attachedImageUri.value = null
        _audioFile.value = null
        _pendingTranscript.value = null

        viewModelScope.launch {
            val messageContent = when {
                text.isNotBlank() -> text
                audio != null -> "🎤 Voice message"
                imageUri != null -> "📷 Image"
                else -> ""
            }

            // Audio arrives as a transcript in engineText — audioFile = null is passed to the
            // engine since Gemma E2B has no audio encoder weights and silently ignores raw files.
            val engineText = buildString {
                if (imageUri != null) append("Analyze the attached image. ")
                if (transcript != null) {
                    append("The farmer said via voice message: \"$transcript\". ")
                } else if (audio != null) {
                    append("The farmer sent a voice message (transcription unavailable). ")
                }
                if (text.isNotBlank()) append(text)
                if (isBlank()) append("Please respond to the farmer's message.")
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

            val result = aiEngine.chat(
                userMessage = engineText,
                imageUri = imageUri,
                audioFile = null,
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
        recognizerTimerJob?.cancel()
        destroyRecognizer()
        aiEngine.release()
    }
}