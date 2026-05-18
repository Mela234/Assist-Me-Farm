package com.cropdoc.app.data.agent

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.cropdoc.app.CropDocApplication
import com.cropdoc.app.MainActivity
import com.cropdoc.app.R
import kotlinx.coroutines.flow.first

class AgentWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private const val TAG = "AgentWorker"
        const val WORK_NAME = "cropdoc_agent"
        const val AGENT_NOTIFICATION_CHANNEL = "cropdoc_agent_alerts"
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "Agent worker running")

        return try {
            val app = CropDocApplication.instance
            val farmRepo = app.farmRepository
            val weatherRepo = app.weatherRepository

            // Gather current context
            val zones = farmRepo.allZones.first()
            val weather = weatherRepo.latestWeather.first()
            val activeZone = farmRepo.activeZone.first()
            val activeCrop = activeZone?.let { farmRepo.getLatestCropForZone(it.id) }
            val latestReading = activeZone?.let { farmRepo.getLatestReadingForZone(it.id) }

            // Build context prompt for agent
            val contextPrompt = buildAgentPrompt(
                zones = zones,
                weather = weather,
                activeCrop = activeCrop,
                latestReading = latestReading
            )

            // Use the singleton engine — same instance used by chat and analysis
            // so it shares the same state and doesn't reinitialize the model
            val engine = app.aiEngine

            Log.d(TAG, "Agent prompt:\n$contextPrompt")

            val alerts = mutableListOf<String>()
            engine.runAgentCheck(contextPrompt) { alert ->
                alerts.add(alert)
            }

            // Show notification for each alert
            alerts.forEach { alert ->
                showAgentNotification(alert)
            }

            Log.d(TAG, "Agent completed with ${alerts.size} alerts")
            Result.success()

        } catch (e: Exception) {
            Log.e(TAG, "Agent worker failed", e)
            Result.retry()
        }
    }

    private fun buildAgentPrompt(
        zones: List<com.cropdoc.app.data.model.FarmZone>,
        weather: com.cropdoc.app.data.model.WeatherData?,
        activeCrop: com.cropdoc.app.data.model.Crop?,
        latestReading: com.cropdoc.app.data.model.SoilReadingHistory?
    ): String {
        return buildString {
            appendLine("You are Farm Assistant's autonomous farm monitoring agent.")
            appendLine("Analyse the current farm conditions and identify critical actions.")
            appendLine("Be concise and practical. Focus on what the farmer needs to do TODAY.")
            appendLine()

            if (zones.isNotEmpty()) {
                appendLine("Farm has ${zones.size} zone(s): ${zones.joinToString { it.name }}")
            }

            weather?.let {
                appendLine()
                appendLine("Current weather at ${it.location}:")
                appendLine("- Temperature: ${it.temperature}°C, Humidity: ${it.humidity}%")
                appendLine("- Expected rainfall: ${it.rainfall}mm")
                appendLine("- Forecast: ${it.forecast}")
            }

            latestReading?.let {
                appendLine()
                appendLine("Latest soil readings:")
                appendLine("- Moisture: ${it.moisture.toInt()}%")
                appendLine("- pH: ${"%.1f".format(it.ph)}")
                appendLine("- Nitrogen: ${it.nitrogen.toInt()} mg/kg")
                appendLine("- Phosphorus: ${it.phosphorus.toInt()} mg/kg")
                appendLine("- Potassium: ${it.potassium.toInt()} mg/kg")
            }

            activeCrop?.let {
                val daysPlanted = ((System.currentTimeMillis() - it.plantedDate) / (1000 * 60 * 60 * 24)).toInt()
                val daysToHarvest = (it.expectedHarvestDays - daysPlanted).coerceAtLeast(0)
                appendLine()
                appendLine("Active crop: ${it.name}")
                appendLine("- Day $daysPlanted of ${it.expectedHarvestDays}")
                appendLine("- Days to harvest: $daysToHarvest")
            }

            appendLine()
            appendLine("Based on these conditions, list only the most important alerts.")
            appendLine("Each alert should be ONE sentence, practical, and actionable.")
            appendLine("Return ONLY a JSON array of alert strings, nothing else:")
            appendLine("""["Alert 1", "Alert 2"]""")
            appendLine("If no alerts needed, return empty array: []")
        }
    }

    private fun showAgentNotification(message: String) {
        ensureChannel()

        val openAppIntent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            appContext, 0, openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(appContext, AGENT_NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🌱 Farm Assistant Alert")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val nm = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun ensureChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                AGENT_NOTIFICATION_CHANNEL,
                "Farm Assistant Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Proactive farm alerts from Farm Assistant agent"
            }
            val nm = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }
}