package com.cropdoc.app.data.db;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0003\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\t\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0014\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\f0\u000bH\'J\u0018\u0010\r\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u000e\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u000fJ\u0010\u0010\u0010\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050\u000bH\'J\u000e\u0010\u0011\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\u0012J\u0016\u0010\u0013\u001a\u00020\b2\u0006\u0010\u0014\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u000fJ\u0016\u0010\u0015\u001a\u00020\u00032\u0006\u0010\u0016\u001a\u00020\u0017H\u00a7@\u00a2\u0006\u0002\u0010\u0018J\u0016\u0010\u0019\u001a\u00020\b2\u0006\u0010\u0016\u001a\u00020\u0017H\u00a7@\u00a2\u0006\u0002\u0010\u0018J\u0016\u0010\u001a\u001a\u00020\b2\u0006\u0010\u0016\u001a\u00020\u0017H\u00a7@\u00a2\u0006\u0002\u0010\u0018J\u001c\u0010\u001b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00170\f0\u000b2\u0006\u0010\u0014\u001a\u00020\u0003H\'J\u0018\u0010\u001c\u001a\u0004\u0018\u00010\u00172\u0006\u0010\u0014\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u000fJ\u0016\u0010\u001d\u001a\u00020\b2\u0006\u0010\u001e\u001a\u00020\u001fH\u00a7@\u00a2\u0006\u0002\u0010 J\u0018\u0010!\u001a\u0004\u0018\u00010\u001f2\u0006\u0010\u0014\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u000fJ&\u0010\"\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001f0\f0\u000b2\u0006\u0010\u0014\u001a\u00020\u00032\b\b\u0002\u0010#\u001a\u00020$H\'J\u0016\u0010%\u001a\u00020\b2\u0006\u0010&\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u000f\u00a8\u0006\'\u00c0\u0006\u0003"}, d2 = {"Lcom/cropdoc/app/data/db/FarmDao;", "", "insertZone", "", "zone", "Lcom/cropdoc/app/data/model/FarmZone;", "(Lcom/cropdoc/app/data/model/FarmZone;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateZone", "", "deleteZone", "getAllZones", "Lkotlinx/coroutines/flow/Flow;", "", "getZoneById", "id", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getActiveZone", "clearActiveZone", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setActiveZone", "zoneId", "insertCrop", "crop", "Lcom/cropdoc/app/data/model/Crop;", "(Lcom/cropdoc/app/data/model/Crop;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateCrop", "deleteCrop", "getCropsForZone", "getLatestCropForZone", "insertSoilReading", "reading", "Lcom/cropdoc/app/data/model/SoilReadingHistory;", "(Lcom/cropdoc/app/data/model/SoilReadingHistory;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getLatestReadingForZone", "getReadingHistoryForZone", "limit", "", "deleteOldReadings", "cutoff", "app_debug"})
@androidx.room.Dao()
public abstract interface FarmDao {
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertZone(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.data.model.FarmZone zone, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateZone(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.data.model.FarmZone zone, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteZone(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.data.model.FarmZone zone, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM farm_zones ORDER BY createdAt ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.cropdoc.app.data.model.FarmZone>> getAllZones();
    
    @androidx.room.Query(value = "SELECT * FROM farm_zones WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getZoneById(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.cropdoc.app.data.model.FarmZone> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM farm_zones WHERE isActiveForSensor = 1 LIMIT 1")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.cropdoc.app.data.model.FarmZone> getActiveZone();
    
    @androidx.room.Query(value = "UPDATE farm_zones SET isActiveForSensor = 0")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object clearActiveZone(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE farm_zones SET isActiveForSensor = 1 WHERE id = :zoneId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object setActiveZone(long zoneId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertCrop(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.data.model.Crop crop, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateCrop(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.data.model.Crop crop, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteCrop(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.data.model.Crop crop, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM crops WHERE zoneId = :zoneId")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.cropdoc.app.data.model.Crop>> getCropsForZone(long zoneId);
    
    @androidx.room.Query(value = "SELECT * FROM crops WHERE zoneId = :zoneId ORDER BY plantedDate DESC LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getLatestCropForZone(long zoneId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.cropdoc.app.data.model.Crop> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertSoilReading(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.data.model.SoilReadingHistory reading, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM soil_reading_history WHERE zoneId = :zoneId ORDER BY timestamp DESC LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getLatestReadingForZone(long zoneId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.cropdoc.app.data.model.SoilReadingHistory> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM soil_reading_history WHERE zoneId = :zoneId ORDER BY timestamp DESC LIMIT :limit")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.cropdoc.app.data.model.SoilReadingHistory>> getReadingHistoryForZone(long zoneId, int limit);
    
    @androidx.room.Query(value = "DELETE FROM soil_reading_history WHERE timestamp < :cutoff")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteOldReadings(long cutoff, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @kotlin.Metadata(mv = {2, 3, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}