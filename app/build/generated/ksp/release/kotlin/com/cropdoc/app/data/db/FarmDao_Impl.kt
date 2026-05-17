package com.cropdoc.app.`data`.db

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.getTotalChangedRows
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.cropdoc.app.`data`.model.Crop
import com.cropdoc.app.`data`.model.FarmZone
import com.cropdoc.app.`data`.model.SoilReadingHistory
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
public class FarmDao_Impl(
  __db: RoomDatabase,
) : FarmDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfFarmZone: EntityInsertAdapter<FarmZone>

  private val __insertAdapterOfCrop: EntityInsertAdapter<Crop>

  private val __insertAdapterOfSoilReadingHistory: EntityInsertAdapter<SoilReadingHistory>

  private val __deleteAdapterOfFarmZone: EntityDeleteOrUpdateAdapter<FarmZone>

  private val __deleteAdapterOfCrop: EntityDeleteOrUpdateAdapter<Crop>

  private val __updateAdapterOfFarmZone: EntityDeleteOrUpdateAdapter<FarmZone>

  private val __updateAdapterOfCrop: EntityDeleteOrUpdateAdapter<Crop>
  init {
    this.__db = __db
    this.__insertAdapterOfFarmZone = object : EntityInsertAdapter<FarmZone>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `farm_zones` (`id`,`name`,`x`,`y`,`width`,`height`,`color`,`isActiveForSensor`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: FarmZone) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindDouble(3, entity.x.toDouble())
        statement.bindDouble(4, entity.y.toDouble())
        statement.bindDouble(5, entity.width.toDouble())
        statement.bindDouble(6, entity.height.toDouble())
        statement.bindLong(7, entity.color)
        val _tmp: Int = if (entity.isActiveForSensor) 1 else 0
        statement.bindLong(8, _tmp.toLong())
        statement.bindLong(9, entity.createdAt)
      }
    }
    this.__insertAdapterOfCrop = object : EntityInsertAdapter<Crop>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `crops` (`id`,`zoneId`,`name`,`plantedDate`,`expectedHarvestDays`,`notes`) VALUES (nullif(?, 0),?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Crop) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.zoneId)
        statement.bindText(3, entity.name)
        statement.bindLong(4, entity.plantedDate)
        statement.bindLong(5, entity.expectedHarvestDays.toLong())
        statement.bindText(6, entity.notes)
      }
    }
    this.__insertAdapterOfSoilReadingHistory = object : EntityInsertAdapter<SoilReadingHistory>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `soil_reading_history` (`id`,`zoneId`,`moisture`,`ph`,`nitrogen`,`phosphorus`,`potassium`,`temperature`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: SoilReadingHistory) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.zoneId)
        statement.bindDouble(3, entity.moisture.toDouble())
        statement.bindDouble(4, entity.ph.toDouble())
        statement.bindDouble(5, entity.nitrogen.toDouble())
        statement.bindDouble(6, entity.phosphorus.toDouble())
        statement.bindDouble(7, entity.potassium.toDouble())
        statement.bindDouble(8, entity.temperature.toDouble())
        statement.bindLong(9, entity.timestamp)
      }
    }
    this.__deleteAdapterOfFarmZone = object : EntityDeleteOrUpdateAdapter<FarmZone>() {
      protected override fun createQuery(): String = "DELETE FROM `farm_zones` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: FarmZone) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__deleteAdapterOfCrop = object : EntityDeleteOrUpdateAdapter<Crop>() {
      protected override fun createQuery(): String = "DELETE FROM `crops` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Crop) {
        statement.bindLong(1, entity.id)
      }
    }
    this.__updateAdapterOfFarmZone = object : EntityDeleteOrUpdateAdapter<FarmZone>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `farm_zones` SET `id` = ?,`name` = ?,`x` = ?,`y` = ?,`width` = ?,`height` = ?,`color` = ?,`isActiveForSensor` = ?,`createdAt` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: FarmZone) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindDouble(3, entity.x.toDouble())
        statement.bindDouble(4, entity.y.toDouble())
        statement.bindDouble(5, entity.width.toDouble())
        statement.bindDouble(6, entity.height.toDouble())
        statement.bindLong(7, entity.color)
        val _tmp: Int = if (entity.isActiveForSensor) 1 else 0
        statement.bindLong(8, _tmp.toLong())
        statement.bindLong(9, entity.createdAt)
        statement.bindLong(10, entity.id)
      }
    }
    this.__updateAdapterOfCrop = object : EntityDeleteOrUpdateAdapter<Crop>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `crops` SET `id` = ?,`zoneId` = ?,`name` = ?,`plantedDate` = ?,`expectedHarvestDays` = ?,`notes` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Crop) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.zoneId)
        statement.bindText(3, entity.name)
        statement.bindLong(4, entity.plantedDate)
        statement.bindLong(5, entity.expectedHarvestDays.toLong())
        statement.bindText(6, entity.notes)
        statement.bindLong(7, entity.id)
      }
    }
  }

  public override suspend fun insertZone(zone: FarmZone): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfFarmZone.insert(_connection, zone)
  }

  public override suspend fun insertCrop(crop: Crop): Unit = performSuspending(__db, false, true) {
      _connection ->
    __insertAdapterOfCrop.insert(_connection, crop)
  }

  public override suspend fun insertSoilReading(reading: SoilReadingHistory): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfSoilReadingHistory.insert(_connection, reading)
  }

  public override suspend fun deleteZone(zone: FarmZone): Unit = performSuspending(__db, false,
      true) { _connection ->
    __deleteAdapterOfFarmZone.handle(_connection, zone)
  }

  public override suspend fun deleteCrop(crop: Crop): Unit = performSuspending(__db, false, true) {
      _connection ->
    __deleteAdapterOfCrop.handle(_connection, crop)
  }

  public override suspend fun updateZone(zone: FarmZone): Unit = performSuspending(__db, false,
      true) { _connection ->
    __updateAdapterOfFarmZone.handle(_connection, zone)
  }

  public override suspend fun updateCrop(crop: Crop): Unit = performSuspending(__db, false, true) {
      _connection ->
    __updateAdapterOfCrop.handle(_connection, crop)
  }

  public override fun getAllZones(): Flow<List<FarmZone>> {
    val _sql: String = "SELECT * FROM farm_zones ORDER BY createdAt ASC"
    return createFlow(__db, false, arrayOf("farm_zones")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfX: Int = getColumnIndexOrThrow(_stmt, "x")
        val _columnIndexOfY: Int = getColumnIndexOrThrow(_stmt, "y")
        val _columnIndexOfWidth: Int = getColumnIndexOrThrow(_stmt, "width")
        val _columnIndexOfHeight: Int = getColumnIndexOrThrow(_stmt, "height")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfIsActiveForSensor: Int = getColumnIndexOrThrow(_stmt, "isActiveForSensor")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<FarmZone> = mutableListOf()
        while (_stmt.step()) {
          val _item: FarmZone
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpX: Float
          _tmpX = _stmt.getDouble(_columnIndexOfX).toFloat()
          val _tmpY: Float
          _tmpY = _stmt.getDouble(_columnIndexOfY).toFloat()
          val _tmpWidth: Float
          _tmpWidth = _stmt.getDouble(_columnIndexOfWidth).toFloat()
          val _tmpHeight: Float
          _tmpHeight = _stmt.getDouble(_columnIndexOfHeight).toFloat()
          val _tmpColor: Long
          _tmpColor = _stmt.getLong(_columnIndexOfColor)
          val _tmpIsActiveForSensor: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsActiveForSensor).toInt()
          _tmpIsActiveForSensor = _tmp != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              FarmZone(_tmpId,_tmpName,_tmpX,_tmpY,_tmpWidth,_tmpHeight,_tmpColor,_tmpIsActiveForSensor,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getZoneById(id: Long): FarmZone? {
    val _sql: String = "SELECT * FROM farm_zones WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfX: Int = getColumnIndexOrThrow(_stmt, "x")
        val _columnIndexOfY: Int = getColumnIndexOrThrow(_stmt, "y")
        val _columnIndexOfWidth: Int = getColumnIndexOrThrow(_stmt, "width")
        val _columnIndexOfHeight: Int = getColumnIndexOrThrow(_stmt, "height")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfIsActiveForSensor: Int = getColumnIndexOrThrow(_stmt, "isActiveForSensor")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: FarmZone?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpX: Float
          _tmpX = _stmt.getDouble(_columnIndexOfX).toFloat()
          val _tmpY: Float
          _tmpY = _stmt.getDouble(_columnIndexOfY).toFloat()
          val _tmpWidth: Float
          _tmpWidth = _stmt.getDouble(_columnIndexOfWidth).toFloat()
          val _tmpHeight: Float
          _tmpHeight = _stmt.getDouble(_columnIndexOfHeight).toFloat()
          val _tmpColor: Long
          _tmpColor = _stmt.getLong(_columnIndexOfColor)
          val _tmpIsActiveForSensor: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsActiveForSensor).toInt()
          _tmpIsActiveForSensor = _tmp != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _result =
              FarmZone(_tmpId,_tmpName,_tmpX,_tmpY,_tmpWidth,_tmpHeight,_tmpColor,_tmpIsActiveForSensor,_tmpCreatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getActiveZone(): Flow<FarmZone?> {
    val _sql: String = "SELECT * FROM farm_zones WHERE isActiveForSensor = 1 LIMIT 1"
    return createFlow(__db, false, arrayOf("farm_zones")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfX: Int = getColumnIndexOrThrow(_stmt, "x")
        val _columnIndexOfY: Int = getColumnIndexOrThrow(_stmt, "y")
        val _columnIndexOfWidth: Int = getColumnIndexOrThrow(_stmt, "width")
        val _columnIndexOfHeight: Int = getColumnIndexOrThrow(_stmt, "height")
        val _columnIndexOfColor: Int = getColumnIndexOrThrow(_stmt, "color")
        val _columnIndexOfIsActiveForSensor: Int = getColumnIndexOrThrow(_stmt, "isActiveForSensor")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: FarmZone?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpX: Float
          _tmpX = _stmt.getDouble(_columnIndexOfX).toFloat()
          val _tmpY: Float
          _tmpY = _stmt.getDouble(_columnIndexOfY).toFloat()
          val _tmpWidth: Float
          _tmpWidth = _stmt.getDouble(_columnIndexOfWidth).toFloat()
          val _tmpHeight: Float
          _tmpHeight = _stmt.getDouble(_columnIndexOfHeight).toFloat()
          val _tmpColor: Long
          _tmpColor = _stmt.getLong(_columnIndexOfColor)
          val _tmpIsActiveForSensor: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsActiveForSensor).toInt()
          _tmpIsActiveForSensor = _tmp != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _result =
              FarmZone(_tmpId,_tmpName,_tmpX,_tmpY,_tmpWidth,_tmpHeight,_tmpColor,_tmpIsActiveForSensor,_tmpCreatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getCropsForZone(zoneId: Long): Flow<List<Crop>> {
    val _sql: String = "SELECT * FROM crops WHERE zoneId = ?"
    return createFlow(__db, false, arrayOf("crops")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, zoneId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfZoneId: Int = getColumnIndexOrThrow(_stmt, "zoneId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfPlantedDate: Int = getColumnIndexOrThrow(_stmt, "plantedDate")
        val _columnIndexOfExpectedHarvestDays: Int = getColumnIndexOrThrow(_stmt,
            "expectedHarvestDays")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _result: MutableList<Crop> = mutableListOf()
        while (_stmt.step()) {
          val _item: Crop
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpZoneId: Long
          _tmpZoneId = _stmt.getLong(_columnIndexOfZoneId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpPlantedDate: Long
          _tmpPlantedDate = _stmt.getLong(_columnIndexOfPlantedDate)
          val _tmpExpectedHarvestDays: Int
          _tmpExpectedHarvestDays = _stmt.getLong(_columnIndexOfExpectedHarvestDays).toInt()
          val _tmpNotes: String
          _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          _item = Crop(_tmpId,_tmpZoneId,_tmpName,_tmpPlantedDate,_tmpExpectedHarvestDays,_tmpNotes)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getLatestCropForZone(zoneId: Long): Crop? {
    val _sql: String = "SELECT * FROM crops WHERE zoneId = ? ORDER BY plantedDate DESC LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, zoneId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfZoneId: Int = getColumnIndexOrThrow(_stmt, "zoneId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfPlantedDate: Int = getColumnIndexOrThrow(_stmt, "plantedDate")
        val _columnIndexOfExpectedHarvestDays: Int = getColumnIndexOrThrow(_stmt,
            "expectedHarvestDays")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _result: Crop?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpZoneId: Long
          _tmpZoneId = _stmt.getLong(_columnIndexOfZoneId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpPlantedDate: Long
          _tmpPlantedDate = _stmt.getLong(_columnIndexOfPlantedDate)
          val _tmpExpectedHarvestDays: Int
          _tmpExpectedHarvestDays = _stmt.getLong(_columnIndexOfExpectedHarvestDays).toInt()
          val _tmpNotes: String
          _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          _result =
              Crop(_tmpId,_tmpZoneId,_tmpName,_tmpPlantedDate,_tmpExpectedHarvestDays,_tmpNotes)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getLatestReadingForZone(zoneId: Long): SoilReadingHistory? {
    val _sql: String =
        "SELECT * FROM soil_reading_history WHERE zoneId = ? ORDER BY timestamp DESC LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, zoneId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfZoneId: Int = getColumnIndexOrThrow(_stmt, "zoneId")
        val _columnIndexOfMoisture: Int = getColumnIndexOrThrow(_stmt, "moisture")
        val _columnIndexOfPh: Int = getColumnIndexOrThrow(_stmt, "ph")
        val _columnIndexOfNitrogen: Int = getColumnIndexOrThrow(_stmt, "nitrogen")
        val _columnIndexOfPhosphorus: Int = getColumnIndexOrThrow(_stmt, "phosphorus")
        val _columnIndexOfPotassium: Int = getColumnIndexOrThrow(_stmt, "potassium")
        val _columnIndexOfTemperature: Int = getColumnIndexOrThrow(_stmt, "temperature")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: SoilReadingHistory?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpZoneId: Long
          _tmpZoneId = _stmt.getLong(_columnIndexOfZoneId)
          val _tmpMoisture: Float
          _tmpMoisture = _stmt.getDouble(_columnIndexOfMoisture).toFloat()
          val _tmpPh: Float
          _tmpPh = _stmt.getDouble(_columnIndexOfPh).toFloat()
          val _tmpNitrogen: Float
          _tmpNitrogen = _stmt.getDouble(_columnIndexOfNitrogen).toFloat()
          val _tmpPhosphorus: Float
          _tmpPhosphorus = _stmt.getDouble(_columnIndexOfPhosphorus).toFloat()
          val _tmpPotassium: Float
          _tmpPotassium = _stmt.getDouble(_columnIndexOfPotassium).toFloat()
          val _tmpTemperature: Float
          _tmpTemperature = _stmt.getDouble(_columnIndexOfTemperature).toFloat()
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _result =
              SoilReadingHistory(_tmpId,_tmpZoneId,_tmpMoisture,_tmpPh,_tmpNitrogen,_tmpPhosphorus,_tmpPotassium,_tmpTemperature,_tmpTimestamp)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getReadingHistoryForZone(zoneId: Long, limit: Int):
      Flow<List<SoilReadingHistory>> {
    val _sql: String =
        "SELECT * FROM soil_reading_history WHERE zoneId = ? ORDER BY timestamp DESC LIMIT ?"
    return createFlow(__db, false, arrayOf("soil_reading_history")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, zoneId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfZoneId: Int = getColumnIndexOrThrow(_stmt, "zoneId")
        val _columnIndexOfMoisture: Int = getColumnIndexOrThrow(_stmt, "moisture")
        val _columnIndexOfPh: Int = getColumnIndexOrThrow(_stmt, "ph")
        val _columnIndexOfNitrogen: Int = getColumnIndexOrThrow(_stmt, "nitrogen")
        val _columnIndexOfPhosphorus: Int = getColumnIndexOrThrow(_stmt, "phosphorus")
        val _columnIndexOfPotassium: Int = getColumnIndexOrThrow(_stmt, "potassium")
        val _columnIndexOfTemperature: Int = getColumnIndexOrThrow(_stmt, "temperature")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<SoilReadingHistory> = mutableListOf()
        while (_stmt.step()) {
          val _item: SoilReadingHistory
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpZoneId: Long
          _tmpZoneId = _stmt.getLong(_columnIndexOfZoneId)
          val _tmpMoisture: Float
          _tmpMoisture = _stmt.getDouble(_columnIndexOfMoisture).toFloat()
          val _tmpPh: Float
          _tmpPh = _stmt.getDouble(_columnIndexOfPh).toFloat()
          val _tmpNitrogen: Float
          _tmpNitrogen = _stmt.getDouble(_columnIndexOfNitrogen).toFloat()
          val _tmpPhosphorus: Float
          _tmpPhosphorus = _stmt.getDouble(_columnIndexOfPhosphorus).toFloat()
          val _tmpPotassium: Float
          _tmpPotassium = _stmt.getDouble(_columnIndexOfPotassium).toFloat()
          val _tmpTemperature: Float
          _tmpTemperature = _stmt.getDouble(_columnIndexOfTemperature).toFloat()
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _item =
              SoilReadingHistory(_tmpId,_tmpZoneId,_tmpMoisture,_tmpPh,_tmpNitrogen,_tmpPhosphorus,_tmpPotassium,_tmpTemperature,_tmpTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearActiveZone(): Int {
    val _sql: String = "UPDATE farm_zones SET isActiveForSensor = 0"
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

  public override suspend fun setActiveZone(zoneId: Long): Int {
    val _sql: String = "UPDATE farm_zones SET isActiveForSensor = 1 WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, zoneId)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteOldReadings(cutoff: Long): Int {
    val _sql: String = "DELETE FROM soil_reading_history WHERE timestamp < ?"
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

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
