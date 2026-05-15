package com.cropdoc.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cropdoc.app.CropDocApplication
import com.cropdoc.app.data.model.WeatherData
import com.cropdoc.app.data.model.WeatherProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CropDocApplication.instance.weatherRepository

    val latestWeather = repository.latestWeather
    val weatherProfile = repository.weatherProfile

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    sealed class SaveState {
        object Idle : SaveState()
        object Saving : SaveState()
        object Success : SaveState()
        data class Error(val message: String) : SaveState()
    }

    fun saveProfile(phoneNumber: String, location: String) {
        if (phoneNumber.isBlank() || location.isBlank()) {
            _saveState.value = SaveState.Error("Phone number and location are required")
            return
        }

        viewModelScope.launch {
            _saveState.value = SaveState.Saving
            try {
                repository.saveProfile(
                    WeatherProfile(
                        phoneNumber = phoneNumber.trim(),
                        location = location.trim()
                    )
                )
                _saveState.value = SaveState.Success
            } catch (e: Exception) {
                _saveState.value = SaveState.Error(e.message ?: "Failed to save")
            }
        }
    }

    fun optOut() {
        viewModelScope.launch {
            repository.deleteProfile()
        }
    }

    fun resetSaveState() {
        _saveState.value = SaveState.Idle
    }
}