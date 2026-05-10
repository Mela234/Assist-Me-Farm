package com.cropdoc.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cropdoc.app.CropDocApplication
import com.cropdoc.app.data.model.Crop
import com.cropdoc.app.data.model.FarmZone
import com.cropdoc.app.data.model.SoilReadingHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FarmViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CropDocApplication.instance.farmRepository

    // ── Zones ─────────────────────────────────────────────────────────────────

    val allZones = repository.allZones
    val activeZone = repository.activeZone

    private val _selectedZone = MutableStateFlow<FarmZone?>(null)
    val selectedZone: StateFlow<FarmZone?> = _selectedZone.asStateFlow()

    private val _selectedZoneCrops = MutableStateFlow<List<Crop>>(emptyList())
    val selectedZoneCrops: StateFlow<List<Crop>> = _selectedZoneCrops.asStateFlow()

    private val _selectedZoneLatestReading = MutableStateFlow<SoilReadingHistory?>(null)
    val selectedZoneLatestReading: StateFlow<SoilReadingHistory?> =
        _selectedZoneLatestReading.asStateFlow()

    // ── Zone operations ───────────────────────────────────────────────────────

    fun addZone(name: String, x: Float, y: Float, width: Float, height: Float, color: Long) {
        viewModelScope.launch {
            repository.addZone(
                FarmZone(
                    name   = name,
                    x      = x,
                    y      = y,
                    width  = width,
                    height = height,
                    color  = color
                )
            )
        }
    }

    fun updateZone(zone: FarmZone) {
        viewModelScope.launch {
            repository.updateZone(zone)
        }
    }

    fun deleteZone(zone: FarmZone) {
        viewModelScope.launch {
            repository.deleteZone(zone)
            if (_selectedZone.value?.id == zone.id) {
                _selectedZone.value = null
            }
        }
    }

    fun selectZone(zone: FarmZone) {
        _selectedZone.value = zone
        viewModelScope.launch {
            repository.getCropsForZone(zone.id).collect { crops ->
                _selectedZoneCrops.value = crops
            }
        }
        viewModelScope.launch {
            _selectedZoneLatestReading.value =
                repository.getLatestReadingForZone(zone.id)
        }
    }

    fun clearSelectedZone() {
        _selectedZone.value = null
        _selectedZoneCrops.value = emptyList()
        _selectedZoneLatestReading.value = null
    }

    fun setActiveZone(zoneId: Long) {
        viewModelScope.launch {
            repository.setActiveZone(zoneId)
        }
    }

    // ── Crop operations ───────────────────────────────────────────────────────

    fun addCrop(
        zoneId: Long,
        name: String,
        plantedDate: Long,
        expectedHarvestDays: Int,
        notes: String = ""
    ) {
        viewModelScope.launch {
            repository.addCrop(
                Crop(
                    zoneId               = zoneId,
                    name                 = name,
                    plantedDate          = plantedDate,
                    expectedHarvestDays  = expectedHarvestDays,
                    notes                = notes
                )
            )
        }
    }

    fun updateCrop(crop: Crop) {
        viewModelScope.launch { repository.updateCrop(crop) }
    }

    fun deleteCrop(crop: Crop) {
        viewModelScope.launch { repository.deleteCrop(crop) }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    fun getDaysPlanted(plantedDate: Long): Int {
        val diff = System.currentTimeMillis() - plantedDate
        return (diff / (1000 * 60 * 60 * 24)).toInt()
    }

    fun getDaysToHarvest(plantedDate: Long, expectedHarvestDays: Int): Int {
        val daysPlanted = getDaysPlanted(plantedDate)
        return (expectedHarvestDays - daysPlanted).coerceAtLeast(0)
    }
}