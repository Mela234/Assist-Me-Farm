package com.cropdoc.app.data.db

import androidx.room.*
import com.cropdoc.app.data.model.WeatherData
import com.cropdoc.app.data.model.WeatherProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherData)

    @Query("SELECT * FROM weather_data ORDER BY timestamp DESC LIMIT 1")
    fun getLatestWeather(): Flow<WeatherData?>

    @Query("SELECT * FROM weather_data ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentWeather(limit: Int = 7): List<WeatherData>

    @Query("DELETE FROM weather_data WHERE timestamp < :cutoff")
    suspend fun deleteOldWeather(cutoff: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: WeatherProfile)

    @Query("SELECT * FROM weather_profile WHERE id = 1")
    fun getProfile(): Flow<WeatherProfile?>

    @Query("DELETE FROM weather_profile")
    suspend fun deleteProfile(): Int
}