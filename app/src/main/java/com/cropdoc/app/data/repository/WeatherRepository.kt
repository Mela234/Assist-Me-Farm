package com.cropdoc.app.data.repository

import com.cropdoc.app.data.db.WeatherDao
import com.cropdoc.app.data.model.WeatherData
import com.cropdoc.app.data.model.WeatherProfile
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

class WeatherRepository(private val dao: WeatherDao) {

    val latestWeather: Flow<WeatherData?> = dao.getLatestWeather()
    val weatherProfile: Flow<WeatherProfile?> = dao.getProfile()

    suspend fun saveWeather(weather: WeatherData) {
        dao.insertWeather(weather)
        val cutoff = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
        dao.deleteOldWeather(cutoff)
    }

    suspend fun saveProfile(profile: WeatherProfile) = dao.saveProfile(profile)

    suspend fun deleteProfile() = dao.deleteProfile()

    suspend fun getRecentWeather(limit: Int = 7) = dao.getRecentWeather(limit)
}