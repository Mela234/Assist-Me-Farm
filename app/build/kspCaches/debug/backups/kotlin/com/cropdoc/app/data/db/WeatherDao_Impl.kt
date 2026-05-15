package com.cropdoc.app.`data`.db

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.getTotalChangedRows
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.cropdoc.app.`data`.model.WeatherData
import com.cropdoc.app.`data`.model.WeatherProfile
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Float
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class WeatherDao_Impl(
  __db: RoomDatabase,
) : WeatherDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfWeatherData: EntityInsertAdapter<WeatherData>

  private val __insertAdapterOfWeatherProfile: EntityInsertAdapter<WeatherProfile>
  init {
    this.__db = __db
    this.__insertAdapterOfWeatherData = object : EntityInsertAdapter<WeatherData>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `weather_data` (`id`,`temperature`,`humidity`,`rainfall`,`windSpeed`,`forecast`,`location`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: WeatherData) {
        statement.bindLong(1, entity.id)
        statement.bindDouble(2, entity.temperature.toDouble())
        statement.bindDouble(3, entity.humidity.toDouble())
        statement.bindDouble(4, entity.rainfall.toDouble())
        statement.bindDouble(5, entity.windSpeed.toDouble())
        statement.bindText(6, entity.forecast)
        statement.bindText(7, entity.location)
        statement.bindLong(8, entity.timestamp)
      }
    }
    this.__insertAdapterOfWeatherProfile = object : EntityInsertAdapter<WeatherProfile>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `weather_profile` (`id`,`phoneNumber`,`location`,`isOptedIn`,`registeredAt`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: WeatherProfile) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindText(2, entity.phoneNumber)
        statement.bindText(3, entity.location)
        val _tmp: Int = if (entity.isOptedIn) 1 else 0
        statement.bindLong(4, _tmp.toLong())
        statement.bindLong(5, entity.registeredAt)
      }
    }
  }

  public override suspend fun insertWeather(weather: WeatherData): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfWeatherData.insert(_connection, weather)
  }

  public override suspend fun saveProfile(profile: WeatherProfile): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfWeatherProfile.insert(_connection, profile)
  }

  public override fun getLatestWeather(): Flow<WeatherData?> {
    val _sql: String = "SELECT * FROM weather_data ORDER BY timestamp DESC LIMIT 1"
    return createFlow(__db, false, arrayOf("weather_data")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTemperature: Int = getColumnIndexOrThrow(_stmt, "temperature")
        val _columnIndexOfHumidity: Int = getColumnIndexOrThrow(_stmt, "humidity")
        val _columnIndexOfRainfall: Int = getColumnIndexOrThrow(_stmt, "rainfall")
        val _columnIndexOfWindSpeed: Int = getColumnIndexOrThrow(_stmt, "windSpeed")
        val _columnIndexOfForecast: Int = getColumnIndexOrThrow(_stmt, "forecast")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: WeatherData?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTemperature: Float
          _tmpTemperature = _stmt.getDouble(_columnIndexOfTemperature).toFloat()
          val _tmpHumidity: Float
          _tmpHumidity = _stmt.getDouble(_columnIndexOfHumidity).toFloat()
          val _tmpRainfall: Float
          _tmpRainfall = _stmt.getDouble(_columnIndexOfRainfall).toFloat()
          val _tmpWindSpeed: Float
          _tmpWindSpeed = _stmt.getDouble(_columnIndexOfWindSpeed).toFloat()
          val _tmpForecast: String
          _tmpForecast = _stmt.getText(_columnIndexOfForecast)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _result =
              WeatherData(_tmpId,_tmpTemperature,_tmpHumidity,_tmpRainfall,_tmpWindSpeed,_tmpForecast,_tmpLocation,_tmpTimestamp)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getRecentWeather(limit: Int): List<WeatherData> {
    val _sql: String = "SELECT * FROM weather_data ORDER BY timestamp DESC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTemperature: Int = getColumnIndexOrThrow(_stmt, "temperature")
        val _columnIndexOfHumidity: Int = getColumnIndexOrThrow(_stmt, "humidity")
        val _columnIndexOfRainfall: Int = getColumnIndexOrThrow(_stmt, "rainfall")
        val _columnIndexOfWindSpeed: Int = getColumnIndexOrThrow(_stmt, "windSpeed")
        val _columnIndexOfForecast: Int = getColumnIndexOrThrow(_stmt, "forecast")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<WeatherData> = mutableListOf()
        while (_stmt.step()) {
          val _item: WeatherData
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTemperature: Float
          _tmpTemperature = _stmt.getDouble(_columnIndexOfTemperature).toFloat()
          val _tmpHumidity: Float
          _tmpHumidity = _stmt.getDouble(_columnIndexOfHumidity).toFloat()
          val _tmpRainfall: Float
          _tmpRainfall = _stmt.getDouble(_columnIndexOfRainfall).toFloat()
          val _tmpWindSpeed: Float
          _tmpWindSpeed = _stmt.getDouble(_columnIndexOfWindSpeed).toFloat()
          val _tmpForecast: String
          _tmpForecast = _stmt.getText(_columnIndexOfForecast)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _item =
              WeatherData(_tmpId,_tmpTemperature,_tmpHumidity,_tmpRainfall,_tmpWindSpeed,_tmpForecast,_tmpLocation,_tmpTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getProfile(): Flow<WeatherProfile?> {
    val _sql: String = "SELECT * FROM weather_profile WHERE id = 1"
    return createFlow(__db, false, arrayOf("weather_profile")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPhoneNumber: Int = getColumnIndexOrThrow(_stmt, "phoneNumber")
        val _columnIndexOfLocation: Int = getColumnIndexOrThrow(_stmt, "location")
        val _columnIndexOfIsOptedIn: Int = getColumnIndexOrThrow(_stmt, "isOptedIn")
        val _columnIndexOfRegisteredAt: Int = getColumnIndexOrThrow(_stmt, "registeredAt")
        val _result: WeatherProfile?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_columnIndexOfId).toInt()
          val _tmpPhoneNumber: String
          _tmpPhoneNumber = _stmt.getText(_columnIndexOfPhoneNumber)
          val _tmpLocation: String
          _tmpLocation = _stmt.getText(_columnIndexOfLocation)
          val _tmpIsOptedIn: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsOptedIn).toInt()
          _tmpIsOptedIn = _tmp != 0
          val _tmpRegisteredAt: Long
          _tmpRegisteredAt = _stmt.getLong(_columnIndexOfRegisteredAt)
          _result =
              WeatherProfile(_tmpId,_tmpPhoneNumber,_tmpLocation,_tmpIsOptedIn,_tmpRegisteredAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteOldWeather(cutoff: Long): Int {
    val _sql: String = "DELETE FROM weather_data WHERE timestamp < ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, cutoff)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteProfile(): Int {
    val _sql: String = "DELETE FROM weather_profile"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
