package com.cropdoc.app.data.repository

import com.cropdoc.app.data.db.ChatDao
import com.cropdoc.app.data.model.ChatMessage
import kotlinx.coroutines.flow.Flow

class ChatRepository(private val dao: ChatDao) {

    fun getGeneralChat(): Flow<List<ChatMessage>> = dao.getGeneralChat()

    fun getChatForZone(zoneId: Long): Flow<List<ChatMessage>> =
        dao.getChatForZone(zoneId)

    suspend fun sendMessage(message: ChatMessage) {
        dao.insertMessage(message)
    }

    suspend fun getRecentMessages(zoneId: Long?, limit: Int = 20): List<ChatMessage> =
        dao.getRecentMessages(zoneId, limit)

    suspend fun clearGeneralChat() = dao.clearGeneralChat()

    suspend fun clearZoneChat(zoneId: Long) = dao.clearZoneChat(zoneId)

    fun getGeneralMessageCount(): Flow<Int> = dao.getGeneralMessageCount()
}