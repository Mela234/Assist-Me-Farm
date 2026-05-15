package com.cropdoc.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ── Weather ───────────────────────────────────────────────────────────────────

@Entity(tableName = "weather_data")
data class WeatherData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val temperature: Float,
    val humidity: Float,
    val rainfall: Float,
    val windSpeed: Float,
    val forecast: String,
    val location: String,
    val timestamp: Long = System.currentTimeMillis()
)

// ── Farm Zone ─────────────────────────────────────────────────────────────────

@Entity(tableName = "farm_zones")
data class FarmZone(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val color: Long,
    val isActiveForSensor: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

// ── Crop ──────────────────────────────────────────────────────────────────────

@Entity(
    tableName = "crops",
    foreignKeys = [ForeignKey(
        entity = FarmZone::class,
        parentColumns = ["id"],
        childColumns = ["zoneId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("zoneId")]
)
data class Crop(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val zoneId: Long,
    val name: String,
    val plantedDate: Long,
    val expectedHarvestDays: Int,
    val notes: String = ""
)

// ── Soil Reading History ──────────────────────────────────────────────────────

@Entity(
    tableName = "soil_reading_history",
    foreignKeys = [ForeignKey(
        entity = FarmZone::class,
        parentColumns = ["id"],
        childColumns = ["zoneId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("zoneId")]
)
data class SoilReadingHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val zoneId: Long,
    val moisture: Float,
    val ph: Float,
    val nitrogen: Float,
    val phosphorus: Float,
    val potassium: Float,
    val temperature: Float,
    val timestamp: Long = System.currentTimeMillis()
)

// ── Chat Message ──────────────────────────────────────────────────────────────

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val role: String,               // "USER" or "ASSISTANT"
    val content: String,
    val attachedImageUri: String? = null,   // file:// path copied to internal storage
    val audioPath: String? = null,          // ← NEW: absolute path to .m4a in filesDir
    val zoneId: Long? = null,
    val contextSnapshot: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

// ── Weather Opt-in Profile ────────────────────────────────────────────────────

@Entity(tableName = "weather_profile")
data class WeatherProfile(
    @PrimaryKey val id: Int = 1,
    val phoneNumber: String,
    val location: String,
    val isOptedIn: Boolean = true,
    val registeredAt: Long = System.currentTimeMillis()
)