package com.cropdoc.app.`data`.db

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.getTotalChangedRows
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.cropdoc.app.`data`.model.ChatMessage
import javax.`annotation`.processing.Generated
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
public class ChatDao_Impl(
  __db: RoomDatabase,
) : ChatDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfChatMessage: EntityInsertAdapter<ChatMessage>

  private val __deleteAdapterOfChatMessage: EntityDeleteOrUpdateAdapter<ChatMessage>
  init {
    this.__db = __db
    this.__insertAdapterOfChatMessage = object : EntityInsertAdapter<ChatMessage>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `chat_messages` (`id`,`role`,`content`,`attachedImageUri`,`audioPath`,`zoneId`,`contextSnapshot`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ChatMessage) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.role)
        statement.bindText(3, entity.content)
        val _tmpAttachedImageUri: String? = entity.attachedImageUri
        if (_tmpAttachedImageUri == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpAttachedImageUri)
        }
        val _tmpAudioPath: String? = entity.audioPath
        if (_tmpAudioPath == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpAudioPath)
        }
        val _tmpZoneId: Long? = entity.zoneId
        if (_tmpZoneId == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpZoneId)
        }
        statement.bindText(7, entity.contextSnapshot)
        statement.bindLong(8, entity.timestamp)
      }
    }
    this.__deleteAdapterOfChatMessage = object : EntityDeleteOrUpdateAdapter<ChatMessage>() {
      protected override fun createQuery(): String = "DELETE FROM `chat_messages` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ChatMessage) {
        statement.bindLong(1, entity.id)
      }
    }
  }

  public override suspend fun insertMessage(message: ChatMessage): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfChatMessage.insert(_connection, message)
  }

  public override suspend fun deleteMessage(message: ChatMessage): Unit = performSuspending(__db,
      false, true) { _connection ->
    __deleteAdapterOfChatMessage.handle(_connection, message)
  }

  public override fun getGeneralChat(): Flow<List<ChatMessage>> {
    val _sql: String = "SELECT * FROM chat_messages WHERE zoneId IS NULL ORDER BY timestamp ASC"
    return createFlow(__db, false, arrayOf("chat_messages")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfRole: Int = getColumnIndexOrThrow(_stmt, "role")
        val _columnIndexOfContent: Int = getColumnIndexOrThrow(_stmt, "content")
        val _columnIndexOfAttachedImageUri: Int = getColumnIndexOrThrow(_stmt, "attachedImageUri")
        val _columnIndexOfAudioPath: Int = getColumnIndexOrThrow(_stmt, "audioPath")
        val _columnIndexOfZoneId: Int = getColumnIndexOrThrow(_stmt, "zoneId")
        val _columnIndexOfContextSnapshot: Int = getColumnIndexOrThrow(_stmt, "contextSnapshot")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<ChatMessage> = mutableListOf()
        while (_stmt.step()) {
          val _item: ChatMessage
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpRole: String
          _tmpRole = _stmt.getText(_columnIndexOfRole)
          val _tmpContent: String
          _tmpContent = _stmt.getText(_columnIndexOfContent)
          val _tmpAttachedImageUri: String?
          if (_stmt.isNull(_columnIndexOfAttachedImageUri)) {
            _tmpAttachedImageUri = null
          } else {
            _tmpAttachedImageUri = _stmt.getText(_columnIndexOfAttachedImageUri)
          }
          val _tmpAudioPath: String?
          if (_stmt.isNull(_columnIndexOfAudioPath)) {
            _tmpAudioPath = null
          } else {
            _tmpAudioPath = _stmt.getText(_columnIndexOfAudioPath)
          }
          val _tmpZoneId: Long?
          if (_stmt.isNull(_columnIndexOfZoneId)) {
            _tmpZoneId = null
          } else {
            _tmpZoneId = _stmt.getLong(_columnIndexOfZoneId)
          }
          val _tmpContextSnapshot: String
          _tmpContextSnapshot = _stmt.getText(_columnIndexOfContextSnapshot)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _item =
              ChatMessage(_tmpId,_tmpRole,_tmpContent,_tmpAttachedImageUri,_tmpAudioPath,_tmpZoneId,_tmpContextSnapshot,_tmpTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getChatForZone(zoneId: Long): Flow<List<ChatMessage>> {
    val _sql: String = "SELECT * FROM chat_messages WHERE zoneId = ? ORDER BY timestamp ASC"
    return createFlow(__db, false, arrayOf("chat_messages")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, zoneId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfRole: Int = getColumnIndexOrThrow(_stmt, "role")
        val _columnIndexOfContent: Int = getColumnIndexOrThrow(_stmt, "content")
        val _columnIndexOfAttachedImageUri: Int = getColumnIndexOrThrow(_stmt, "attachedImageUri")
        val _columnIndexOfAudioPath: Int = getColumnIndexOrThrow(_stmt, "audioPath")
        val _columnIndexOfZoneId: Int = getColumnIndexOrThrow(_stmt, "zoneId")
        val _columnIndexOfContextSnapshot: Int = getColumnIndexOrThrow(_stmt, "contextSnapshot")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<ChatMessage> = mutableListOf()
        while (_stmt.step()) {
          val _item: ChatMessage
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpRole: String
          _tmpRole = _stmt.getText(_columnIndexOfRole)
          val _tmpContent: String
          _tmpContent = _stmt.getText(_columnIndexOfContent)
          val _tmpAttachedImageUri: String?
          if (_stmt.isNull(_columnIndexOfAttachedImageUri)) {
            _tmpAttachedImageUri = null
          } else {
            _tmpAttachedImageUri = _stmt.getText(_columnIndexOfAttachedImageUri)
          }
          val _tmpAudioPath: String?
          if (_stmt.isNull(_columnIndexOfAudioPath)) {
            _tmpAudioPath = null
          } else {
            _tmpAudioPath = _stmt.getText(_columnIndexOfAudioPath)
          }
          val _tmpZoneId: Long?
          if (_stmt.isNull(_columnIndexOfZoneId)) {
            _tmpZoneId = null
          } else {
            _tmpZoneId = _stmt.getLong(_columnIndexOfZoneId)
          }
          val _tmpContextSnapshot: String
          _tmpContextSnapshot = _stmt.getText(_columnIndexOfContextSnapshot)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _item =
              ChatMessage(_tmpId,_tmpRole,_tmpContent,_tmpAttachedImageUri,_tmpAudioPath,_tmpZoneId,_tmpContextSnapshot,_tmpTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getRecentMessages(zoneId: Long?, limit: Int): List<ChatMessage> {
    val _sql: String = """
        |
        |        SELECT * FROM chat_messages 
        |        WHERE (zoneId IS NULL AND ? IS NULL) 
        |           OR zoneId = ? 
        |        ORDER BY timestamp DESC 
        |        LIMIT ?
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        if (zoneId == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, zoneId)
        }
        _argIndex = 2
        if (zoneId == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, zoneId)
        }
        _argIndex = 3
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfRole: Int = getColumnIndexOrThrow(_stmt, "role")
        val _columnIndexOfContent: Int = getColumnIndexOrThrow(_stmt, "content")
        val _columnIndexOfAttachedImageUri: Int = getColumnIndexOrThrow(_stmt, "attachedImageUri")
        val _columnIndexOfAudioPath: Int = getColumnIndexOrThrow(_stmt, "audioPath")
        val _columnIndexOfZoneId: Int = getColumnIndexOrThrow(_stmt, "zoneId")
        val _columnIndexOfContextSnapshot: Int = getColumnIndexOrThrow(_stmt, "contextSnapshot")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<ChatMessage> = mutableListOf()
        while (_stmt.step()) {
          val _item: ChatMessage
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpRole: String
          _tmpRole = _stmt.getText(_columnIndexOfRole)
          val _tmpContent: String
          _tmpContent = _stmt.getText(_columnIndexOfContent)
          val _tmpAttachedImageUri: String?
          if (_stmt.isNull(_columnIndexOfAttachedImageUri)) {
            _tmpAttachedImageUri = null
          } else {
            _tmpAttachedImageUri = _stmt.getText(_columnIndexOfAttachedImageUri)
          }
          val _tmpAudioPath: String?
          if (_stmt.isNull(_columnIndexOfAudioPath)) {
            _tmpAudioPath = null
          } else {
            _tmpAudioPath = _stmt.getText(_columnIndexOfAudioPath)
          }
          val _tmpZoneId: Long?
          if (_stmt.isNull(_columnIndexOfZoneId)) {
            _tmpZoneId = null
          } else {
            _tmpZoneId = _stmt.getLong(_columnIndexOfZoneId)
          }
          val _tmpContextSnapshot: String
          _tmpContextSnapshot = _stmt.getText(_columnIndexOfContextSnapshot)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _item =
              ChatMessage(_tmpId,_tmpRole,_tmpContent,_tmpAttachedImageUri,_tmpAudioPath,_tmpZoneId,_tmpContextSnapshot,_tmpTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getGeneralMessageCount(): Flow<Int> {
    val _sql: String = "SELECT COUNT(*) FROM chat_messages WHERE zoneId IS NULL"
    return createFlow(__db, false, arrayOf("chat_messages")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearGeneralChat(): Int {
    val _sql: String = "DELETE FROM chat_messages WHERE zoneId IS NULL"
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

  public override suspend fun clearZoneChat(zoneId: Long): Int {
    val _sql: String = "DELETE FROM chat_messages WHERE zoneId = ?"
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

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
