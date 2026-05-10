package com.cropdoc.app.data.db

import androidx.room.*
import com.cropdoc.app.data.model.ChatMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessage)

    @Delete
    suspend fun deleteMessage(message: ChatMessage)

    @Query("SELECT * FROM chat_messages WHERE zoneId IS NULL ORDER BY timestamp ASC")
    fun getGeneralChat(): Flow<List<ChatMessage>>

    @Query("SELECT * FROM chat_messages WHERE zoneId = :zoneId ORDER BY timestamp ASC")
    fun getChatForZone(zoneId: Long): Flow<List<ChatMessage>>

    @Query("""
        SELECT * FROM chat_messages 
        WHERE (zoneId IS NULL AND :zoneId IS NULL) 
           OR zoneId = :zoneId 
        ORDER BY timestamp DESC 
        LIMIT :limit
    """)
    suspend fun getRecentMessages(zoneId: Long?, limit: Int = 20): List<ChatMessage>

    @Query("DELETE FROM chat_messages WHERE zoneId IS NULL")
    suspend fun clearGeneralChat(): Int

    @Query("DELETE FROM chat_messages WHERE zoneId = :zoneId")
    suspend fun clearZoneChat(zoneId: Long): Int

    @Query("SELECT COUNT(*) FROM chat_messages WHERE zoneId IS NULL")
    fun getGeneralMessageCount(): Flow<Int>
}