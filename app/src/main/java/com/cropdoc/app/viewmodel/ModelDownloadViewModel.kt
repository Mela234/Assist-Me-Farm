package com.cropdoc.app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class ModelDownloadViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "ModelDownload"
        const val MODEL_FILENAME = "gemma-4-E2B-it.litertlm"
        const val MODEL_URL = "https://huggingface.co/litert-community/gemma-4-E2B-it-litert-lm/resolve/main/gemma-4-E2B-it.litertlm"
        const val MODEL_SIZE_GB = 2.5f
    }

    sealed class DownloadState {
        object Idle : DownloadState()
        object Checking : DownloadState()
        object AlreadyExists : DownloadState()
        data class Downloading(val progress: Float, val downloadedMb: Float, val totalMb: Float) : DownloadState()
        object Processing : DownloadState()
        object Success : DownloadState()
        data class Error(val message: String) : DownloadState()
    }

    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val downloadState: StateFlow<DownloadState> = _downloadState.asStateFlow()

    private var downloadJob: Job? = null

    init {
        checkModelExists()
    }

    private fun checkModelExists() {
        viewModelScope.launch {
            _downloadState.value = DownloadState.Checking
            val modelFile = File(getApplication<Application>().filesDir, MODEL_FILENAME)
            if (modelFile.exists() && modelFile.length() > 0) {
                _downloadState.value = DownloadState.AlreadyExists
            } else {
                _downloadState.value = DownloadState.Idle
            }
        }
    }

    fun startDownload() {
        if (downloadJob?.isActive == true) return

        downloadJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val modelFile = File(getApplication<Application>().filesDir, MODEL_FILENAME)
                val tempFile = File(getApplication<Application>().filesDir, "$MODEL_FILENAME.tmp")

                // Resume support — check if temp file exists
                val startByte = if (tempFile.exists()) tempFile.length() else 0L

                val url = URL(MODEL_URL)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 30_000
                connection.readTimeout = 30_000

                // Request partial content if resuming
                if (startByte > 0) {
                    connection.setRequestProperty("Range", "bytes=$startByte-")
                }

                connection.connect()

                val totalBytes = if (startByte > 0) {
                    startByte + connection.contentLengthLong
                } else {
                    connection.contentLengthLong
                }

                val totalMb = totalBytes / (1024f * 1024f)

                val input = connection.inputStream
                val output = FileOutputStream(tempFile, startByte > 0)

                val buffer = ByteArray(8192)
                var downloaded = startByte
                var bytesRead: Int

                while (input.read(buffer).also { bytesRead = it } != -1) {
                    output.write(buffer, 0, bytesRead)
                    downloaded += bytesRead

                    val progress = downloaded.toFloat() / totalBytes.toFloat()
                    val downloadedMb = downloaded / (1024f * 1024f)

                    withContext(Dispatchers.Main) {
                        _downloadState.value = DownloadState.Downloading(
                            progress = progress.coerceIn(0f, 1f),
                            downloadedMb = downloadedMb,
                            totalMb = totalMb
                        )
                    }
                }

                output.close()
                input.close()
                connection.disconnect()

                // Move temp file to final location
                withContext(Dispatchers.Main) {
                    _downloadState.value = DownloadState.Processing
                }

                tempFile.renameTo(modelFile)

                Log.d(TAG, "Model downloaded to ${modelFile.absolutePath}")

                withContext(Dispatchers.Main) {
                    _downloadState.value = DownloadState.Success
                }

            } catch (e: Exception) {
                Log.e(TAG, "Download failed", e)
                withContext(Dispatchers.Main) {
                    _downloadState.value = DownloadState.Error(e.message ?: "Download failed")
                }
            }
        }
    }

    fun cancelDownload() {
        downloadJob?.cancel()
        _downloadState.value = DownloadState.Idle
    }

    fun retry() {
        _downloadState.value = DownloadState.Idle
    }
}