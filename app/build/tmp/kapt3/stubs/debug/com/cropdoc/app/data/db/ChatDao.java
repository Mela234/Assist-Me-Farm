package com.cropdoc.app.data.db;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0007\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0014\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u000b0\nH\'J\u001c\u0010\f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u000b0\n2\u0006\u0010\r\u001a\u00020\u0003H\'J(\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00050\u000b2\b\u0010\r\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u000f\u001a\u00020\u0010H\u00a7@\u00a2\u0006\u0002\u0010\u0011J\u000e\u0010\u0012\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\u0013J\u0016\u0010\u0014\u001a\u00020\b2\u0006\u0010\r\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0015J\u000e\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00100\nH\'\u00a8\u0006\u0017\u00c0\u0006\u0003"}, d2 = {"Lcom/cropdoc/app/data/db/ChatDao;", "", "insertMessage", "", "message", "Lcom/cropdoc/app/data/model/ChatMessage;", "(Lcom/cropdoc/app/data/model/ChatMessage;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteMessage", "", "getGeneralChat", "Lkotlinx/coroutines/flow/Flow;", "", "getChatForZone", "zoneId", "getRecentMessages", "limit", "", "(Ljava/lang/Long;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "clearGeneralChat", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "clearZoneChat", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getGeneralMessageCount", "app_debug"})
@androidx.room.Dao()
public abstract interface ChatDao {
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertMessage(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.data.model.ChatMessage message, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteMessage(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.data.model.ChatMessage message, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM chat_messages WHERE zoneId IS NULL ORDER BY timestamp ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.cropdoc.app.data.model.ChatMessage>> getGeneralChat();
    
    @androidx.room.Query(value = "SELECT * FROM chat_messages WHERE zoneId = :zoneId ORDER BY timestamp ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.cropdoc.app.data.model.ChatMessage>> getChatForZone(long zoneId);
    
    @androidx.room.Query(value = "\n        SELECT * FROM chat_messages \n        WHERE (zoneId IS NULL AND :zoneId IS NULL) \n           OR zoneId = :zoneId \n        ORDER BY timestamp DESC \n        LIMIT :limit\n    ")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getRecentMessages(@org.jetbrains.annotations.Nullable()
    java.lang.Long zoneId, int limit, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.cropdoc.app.data.model.ChatMessage>> $completion);
    
    @androidx.room.Query(value = "DELETE FROM chat_messages WHERE zoneId IS NULL")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object clearGeneralChat(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM chat_messages WHERE zoneId = :zoneId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object clearZoneChat(long zoneId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM chat_messages WHERE zoneId IS NULL")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.lang.Integer> getGeneralMessageCount();
    
    @kotlin.Metadata(mv = {2, 3, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}