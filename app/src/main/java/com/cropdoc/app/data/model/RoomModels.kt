package com.cropdoc.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ── Weather ───────────────────────────────────────────────────────────────────

@Entity(tableName = "weather_data")
data class WeatherData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val temperature: Float,         // °C
    val humidity: Float,            // %
    val rainfall: Float,            // mm expected
    val windSpeed: Float,           // km/h
    val forecast: String,           // plain text summary
    val location: String,
    val timestamp: Long = System.currentTimeMillis()
)

// ── Farm Zone ─────────────────────────────────────────────────────────────────

@Entity(tableName = "farm_zones")
data class FarmZone(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val x: Float,                   // position on canvas
    val y: Float,
    val width: Float,
    val height: Float,
    val color: Long,                // ARGB color stored as Long
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
    val name: String,               // e.g. "Maize"
    val plantedDate: Long,          // timestamp
    val expectedHarvestDays: Int,   // e.g. 90 days
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
    val attachedImageUri: String? = null,
    val zoneId: Long? = null,       // null = general chat, non-null = zone chat
    val contextSnapshot: String = "", // JSON snapshot of soil+weather+farm at time
    val timestamp: Long = System.currentTimeMillis()
)

// ── Weather Opt-in Profile ────────────────────────────────────────────────────

@Entity(tableName = "weather_profile")
data class WeatherProfile(
    @PrimaryKey val id: Int = 1,    // single row
    val phoneNumber: String,
    val location: String,
    val isOptedIn: Boolean = true,
    val registeredAt: Long = System.currentTimeMillis()
)