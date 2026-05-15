package com.cropdoc.app.data.db

import androidx.room.*
import com.cropdoc.app.data.model.Crop
import com.cropdoc.app.data.model.FarmZone
import com.cropdoc.app.data.model.SoilReadingHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertZone(zone: FarmZone)

    @Update
    suspend fun updateZone(zone: FarmZone)

    @Delete
    suspend fun deleteZone(zone: FarmZone)

    @Query("SELECT * FROM farm_zones ORDER BY createdAt ASC")
    fun getAllZones(): Flow<List<FarmZone>>

    @Query("SELECT * FROM farm_zones WHERE id = :id")
    suspend fun getZoneById(id: Long): FarmZone?

    @Query("SELECT * FROM farm_zones WHERE isActiveForSensor = 1 LIMIT 1")
    fun getActiveZone(): Flow<FarmZone?>

    @Query("UPDATE farm_zones SET isActiveForSensor = 0")
    suspend fun clearActiveZone(): Int

    @Query("UPDATE farm_zones SET isActiveForSensor = 1 WHERE id = :zoneId")
    suspend fun setActiveZone(zoneId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrop(crop: Crop)

    @Update
    suspend fun updateCrop(crop: Crop)

    @Delete
    suspend fun deleteCrop(crop: Crop)

    @Query("SELECT * FROM crops WHERE zoneId = :zoneId")
    fun getCropsForZone(zoneId: Long): Flow<List<Crop>>

    @Query("SELECT * FROM crops WHERE zoneId = :zoneId ORDER BY plantedDate DESC LIMIT 1")
    suspend fun getLatestCropForZone(zoneId: Long): Crop?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSoilReading(reading: SoilReadingHistory)

    @Query("SELECT * FROM soil_reading_history WHERE zoneId = :zoneId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestReadingForZone(zoneId: Long): SoilReadingHistory?

    @Query("SELECT * FROM soil_reading_history WHERE zoneId = :zoneId ORDER BY timestamp DESC LIMIT :limit")
    fun getReadingHistoryForZone(zoneId: Long, limit: Int = 20): Flow<List<SoilReadingHistory>>

    @Query("DELETE FROM soil_reading_history WHERE timestamp < :cutoff")
    suspend fun deleteOldReadings(cutoff: Long): Int
}