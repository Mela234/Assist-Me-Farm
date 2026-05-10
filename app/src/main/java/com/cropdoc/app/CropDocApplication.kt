package com.cropdoc.app

import android.app.Application
import com.cropdoc.app.data.db.CropDocDatabase
import com.cropdoc.app.data.model.CropDocAiEngine
import com.cropdoc.app.data.repository.ChatRepository
import com.cropdoc.app.data.repository.WeatherRepository
import com.cropdoc.app.data.repository.FarmRepository
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.cropdoc.app.data.agent.AgentWorker
import java.util.concurrent.TimeUnit

class CropDocApplication : Application() {

    // Database singleton
    val database by lazy { CropDocDatabase.getInstance(this) }

    // Repositories
    val weatherRepository by lazy { WeatherRepository(database.weatherDao()) }
    val farmRepository by lazy { FarmRepository(database.farmDao()) }
    val chatRepository by lazy { ChatRepository(database.chatDao()) }

    // AI Engine singleton — shared across ViewModels
    val aiEngine by lazy { CropDocAiEngine(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun scheduleAgent() {
        val request = PeriodicWorkRequestBuilder<AgentWorker>(12, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            AgentWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun cancelAgent() {
        WorkManager.getInstance(this).cancelUniqueWork(AgentWorker.WORK_NAME)
    }

    companion object {
        lateinit var instance: CropDocApplication
            private set
    }
}