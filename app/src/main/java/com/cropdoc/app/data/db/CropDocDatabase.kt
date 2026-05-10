package com.cropdoc.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cropdoc.app.data.model.*

@Database(
    entities = [
        WeatherData::class,
        WeatherProfile::class,
        FarmZone::class,
        Crop::class,
        SoilReadingHistory::class,
        ChatMessage::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CropDocDatabase : RoomDatabase() {

    abstract fun weatherDao(): WeatherDao
    abstract fun farmDao(): FarmDao
    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile
        private var INSTANCE: CropDocDatabase? = null

        fun getInstance(context: Context): CropDocDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    CropDocDatabase::class.java,
                    "cropdoc_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}