package com.cropdoc.app.`data`.db

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class CropDocDatabase_Impl : CropDocDatabase() {
  private val _weatherDao: Lazy<WeatherDao> = lazy {
    WeatherDao_Impl(this)
  }

  private val _farmDao: Lazy<FarmDao> = lazy {
    FarmDao_Impl(this)
  }

  private val _chatDao: Lazy<ChatDao> = lazy {
    ChatDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(2,
        "73f26ae80bcefbf538138a786b19cc98", "f24fe06f1927ad9b8bdef22ad92e14b1") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `weather_data` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `temperature` REAL NOT NULL, `humidity` REAL NOT NULL, `rainfall` REAL NOT NULL, `windSpeed` REAL NOT NULL, `forecast` TEXT NOT NULL, `location` TEXT NOT NULL, `timestamp` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `weather_profile` (`id` INTEGER NOT NULL, `phoneNumber` TEXT NOT NULL, `location` TEXT NOT NULL, `isOptedIn` INTEGER NOT NULL, `registeredAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `farm_zones` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `x` REAL NOT NULL, `y` REAL NOT NULL, `width` REAL NOT NULL, `height` REAL NOT NULL, `color` INTEGER NOT NULL, `isActiveForSensor` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `crops` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `zoneId` INTEGER NOT NULL, `name` TEXT NOT NULL, `plantedDate` INTEGER NOT NULL, `expectedHarvestDays` INTEGER NOT NULL, `notes` TEXT NOT NULL, FOREIGN KEY(`zoneId`) REFERENCES `farm_zones`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_crops_zoneId` ON `crops` (`zoneId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `soil_reading_history` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `zoneId` INTEGER NOT NULL, `moisture` REAL NOT NULL, `ph` REAL NOT NULL, `nitrogen` REAL NOT NULL, `phosphorus` REAL NOT NULL, `potassium` REAL NOT NULL, `temperature` REAL NOT NULL, `timestamp` INTEGER NOT NULL, FOREIGN KEY(`zoneId`) REFERENCES `farm_zones`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
        connection.execSQL("CREATE INDEX IF NOT EXISTS `index_soil_reading_history_zoneId` ON `soil_reading_history` (`zoneId`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `chat_messages` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `role` TEXT NOT NULL, `content` TEXT NOT NULL, `attachedImageUri` TEXT, `audioPath` TEXT, `zoneId` INTEGER, `contextSnapshot` TEXT NOT NULL, `timestamp` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '73f26ae80bcefbf538138a786b19cc98')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `weather_data`")
        connection.execSQL("DROP TABLE IF EXISTS `weather_profile`")
        connection.execSQL("DROP TABLE IF EXISTS `farm_zones`")
        connection.execSQL("DROP TABLE IF EXISTS `crops`")
        connection.execSQL("DROP TABLE IF EXISTS `soil_reading_history`")
        connection.execSQL("DROP TABLE IF EXISTS `chat_messages`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        connection.execSQL("PRAGMA foreign_keys = ON")
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsWeatherData: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsWeatherData.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWeatherData.put("temperature", TableInfo.Column("temperature", "REAL", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWeatherData.put("humidity", TableInfo.Column("humidity", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWeatherData.put("rainfall", TableInfo.Column("rainfall", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWeatherData.put("windSpeed", TableInfo.Column("windSpeed", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWeatherData.put("forecast", TableInfo.Column("forecast", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWeatherData.put("location", TableInfo.Column("location", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWeatherData.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysWeatherData: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesWeatherData: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoWeatherData: TableInfo = TableInfo("weather_data", _columnsWeatherData,
            _foreignKeysWeatherData, _indicesWeatherData)
        val _existingWeatherData: TableInfo = read(connection, "weather_data")
        if (!_infoWeatherData.equals(_existingWeatherData)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |weather_data(com.cropdoc.app.data.model.WeatherData).
              | Expected:
              |""".trimMargin() + _infoWeatherData + """
              |
              | Found:
              |""".trimMargin() + _existingWeatherData)
        }
        val _columnsWeatherProfile: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsWeatherProfile.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWeatherProfile.put("phoneNumber", TableInfo.Column("phoneNumber", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWeatherProfile.put("location", TableInfo.Column("location", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsWeatherProfile.put("isOptedIn", TableInfo.Column("isOptedIn", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsWeatherProfile.put("registeredAt", TableInfo.Column("registeredAt", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysWeatherProfile: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesWeatherProfile: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoWeatherProfile: TableInfo = TableInfo("weather_profile", _columnsWeatherProfile,
            _foreignKeysWeatherProfile, _indicesWeatherProfile)
        val _existingWeatherProfile: TableInfo = read(connection, "weather_profile")
        if (!_infoWeatherProfile.equals(_existingWeatherProfile)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |weather_profile(com.cropdoc.app.data.model.WeatherProfile).
              | Expected:
              |""".trimMargin() + _infoWeatherProfile + """
              |
              | Found:
              |""".trimMargin() + _existingWeatherProfile)
        }
        val _columnsFarmZones: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsFarmZones.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmZones.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmZones.put("x", TableInfo.Column("x", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmZones.put("y", TableInfo.Column("y", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmZones.put("width", TableInfo.Column("width", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmZones.put("height", TableInfo.Column("height", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmZones.put("color", TableInfo.Column("color", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmZones.put("isActiveForSensor", TableInfo.Column("isActiveForSensor", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsFarmZones.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysFarmZones: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesFarmZones: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoFarmZones: TableInfo = TableInfo("farm_zones", _columnsFarmZones,
            _foreignKeysFarmZones, _indicesFarmZones)
        val _existingFarmZones: TableInfo = read(connection, "farm_zones")
        if (!_infoFarmZones.equals(_existingFarmZones)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |farm_zones(com.cropdoc.app.data.model.FarmZone).
              | Expected:
              |""".trimMargin() + _infoFarmZones + """
              |
              | Found:
              |""".trimMargin() + _existingFarmZones)
        }
        val _columnsCrops: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCrops.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCrops.put("zoneId", TableInfo.Column("zoneId", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCrops.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCrops.put("plantedDate", TableInfo.Column("plantedDate", "INTEGER", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCrops.put("expectedHarvestDays", TableInfo.Column("expectedHarvestDays", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsCrops.put("notes", TableInfo.Column("notes", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCrops: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysCrops.add(TableInfo.ForeignKey("farm_zones", "CASCADE", "NO ACTION",
            listOf("zoneId"), listOf("id")))
        val _indicesCrops: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesCrops.add(TableInfo.Index("index_crops_zoneId", false, listOf("zoneId"),
            listOf("ASC")))
        val _infoCrops: TableInfo = TableInfo("crops", _columnsCrops, _foreignKeysCrops,
            _indicesCrops)
        val _existingCrops: TableInfo = read(connection, "crops")
        if (!_infoCrops.equals(_existingCrops)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |crops(com.cropdoc.app.data.model.Crop).
              | Expected:
              |""".trimMargin() + _infoCrops + """
              |
              | Found:
              |""".trimMargin() + _existingCrops)
        }
        val _columnsSoilReadingHistory: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsSoilReadingHistory.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSoilReadingHistory.put("zoneId", TableInfo.Column("zoneId", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSoilReadingHistory.put("moisture", TableInfo.Column("moisture", "REAL", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSoilReadingHistory.put("ph", TableInfo.Column("ph", "REAL", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsSoilReadingHistory.put("nitrogen", TableInfo.Column("nitrogen", "REAL", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSoilReadingHistory.put("phosphorus", TableInfo.Column("phosphorus", "REAL", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSoilReadingHistory.put("potassium", TableInfo.Column("potassium", "REAL", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSoilReadingHistory.put("temperature", TableInfo.Column("temperature", "REAL", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsSoilReadingHistory.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysSoilReadingHistory: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        _foreignKeysSoilReadingHistory.add(TableInfo.ForeignKey("farm_zones", "CASCADE",
            "NO ACTION", listOf("zoneId"), listOf("id")))
        val _indicesSoilReadingHistory: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesSoilReadingHistory.add(TableInfo.Index("index_soil_reading_history_zoneId", false,
            listOf("zoneId"), listOf("ASC")))
        val _infoSoilReadingHistory: TableInfo = TableInfo("soil_reading_history",
            _columnsSoilReadingHistory, _foreignKeysSoilReadingHistory, _indicesSoilReadingHistory)
        val _existingSoilReadingHistory: TableInfo = read(connection, "soil_reading_history")
        if (!_infoSoilReadingHistory.equals(_existingSoilReadingHistory)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |soil_reading_history(com.cropdoc.app.data.model.SoilReadingHistory).
              | Expected:
              |""".trimMargin() + _infoSoilReadingHistory + """
              |
              | Found:
              |""".trimMargin() + _existingSoilReadingHistory)
        }
        val _columnsChatMessages: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsChatMessages.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChatMessages.put("role", TableInfo.Column("role", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChatMessages.put("content", TableInfo.Column("content", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChatMessages.put("attachedImageUri", TableInfo.Column("attachedImageUri", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChatMessages.put("audioPath", TableInfo.Column("audioPath", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChatMessages.put("zoneId", TableInfo.Column("zoneId", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChatMessages.put("contextSnapshot", TableInfo.Column("contextSnapshot", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChatMessages.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysChatMessages: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesChatMessages: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoChatMessages: TableInfo = TableInfo("chat_messages", _columnsChatMessages,
            _foreignKeysChatMessages, _indicesChatMessages)
        val _existingChatMessages: TableInfo = read(connection, "chat_messages")
        if (!_infoChatMessages.equals(_existingChatMessages)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |chat_messages(com.cropdoc.app.data.model.ChatMessage).
              | Expected:
              |""".trimMargin() + _infoChatMessages + """
              |
              | Found:
              |""".trimMargin() + _existingChatMessages)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "weather_data",
        "weather_profile", "farm_zones", "crops", "soil_reading_history", "chat_messages")
  }

  public override fun clearAllTables() {
    super.performClear(true, "weather_data", "weather_profile", "farm_zones", "crops",
        "soil_reading_history", "chat_messages")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(WeatherDao::class, WeatherDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(FarmDao::class, FarmDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ChatDao::class, ChatDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun weatherDao(): WeatherDao = _weatherDao.value

  public override fun farmDao(): FarmDao = _farmDao.value

  public override fun chatDao(): ChatDao = _chatDao.value
}
