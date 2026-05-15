package com.cropdoc.app.data.repository

import com.cropdoc.app.data.db.FarmDao
import com.cropdoc.app.data.model.Crop
import com.cropdoc.app.data.model.FarmZone
import com.cropdoc.app.data.model.SoilReading
import com.cropdoc.app.data.model.SoilReadingHistory
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

class FarmRepository(private val dao: FarmDao) {

    val allZones: Flow<List<FarmZone>> = dao.getAllZones()
    val activeZone: Flow<FarmZone?> = dao.getActiveZone()

    // ── Zones ─────────────────────────────────────────────────────────────────

    suspend fun addZone(zone: FarmZone) = dao.insertZone(zone)

    suspend fun updateZone(zone: FarmZone) = dao.updateZone(zone)

    suspend fun deleteZone(zone: FarmZone) = dao.deleteZone(zone)

    suspend fun getZoneById(id: Long) = dao.getZoneById(id)

    suspend fun setActiveZone(zoneId: Long) {
        dao.clearActiveZone()
        dao.setActiveZone(zoneId)
    }

    // ── Crops ─────────────────────────────────────────────────────────────────

    fun getCropsForZone(zoneId: Long): Flow<List<Crop>> =
        dao.getCropsForZone(zoneId)

    suspend fun addCrop(crop: Crop) = dao.insertCrop(crop)

    suspend fun updateCrop(crop: Crop) = dao.updateCrop(crop)

    suspend fun deleteCrop(crop: Crop) = dao.deleteCrop(crop)

    suspend fun getLatestCropForZone(zoneId: Long) =
        dao.getLatestCropForZone(zoneId)

    // ── Soil History ──────────────────────────────────────────────────────────

    suspend fun saveReading(zoneId: Long, reading: SoilReading) {
        dao.insertSoilReading(
            SoilReadingHistory(
                zoneId      = zoneId,
                moisture    = reading.moisture,
                ph          = reading.ph,
                nitrogen    = reading.nitrogen,
                phosphorus  = reading.phosphorus,
                potassium   = reading.potassium,
                temperature = reading.temperature
            )
        )
        val cutoff = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30)
        dao.deleteOldReadings(cutoff)
    }

    suspend fun getLatestReadingForZone(zoneId: Long) =
        dao.getLatestReadingForZone(zoneId)

    fun getReadingHistoryForZone(zoneId: Long, limit: Int = 20) =
        dao.getReadingHistoryForZone(zoneId, limit)
}