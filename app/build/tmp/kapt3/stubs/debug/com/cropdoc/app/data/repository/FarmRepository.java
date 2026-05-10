package com.cropdoc.app.data.repository;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0016\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\tH\u0086@\u00a2\u0006\u0002\u0010\u0011J\u0016\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0010\u001a\u00020\tH\u0086@\u00a2\u0006\u0002\u0010\u0011J\u0016\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0010\u001a\u00020\tH\u0086@\u00a2\u0006\u0002\u0010\u0011J\u0018\u0010\u0015\u001a\u0004\u0018\u00010\t2\u0006\u0010\u0016\u001a\u00020\u000fH\u0086@\u00a2\u0006\u0002\u0010\u0017J\u0016\u0010\u0018\u001a\u00020\u00132\u0006\u0010\u0019\u001a\u00020\u000fH\u0086@\u00a2\u0006\u0002\u0010\u0017J\u001a\u0010\u001a\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001b0\b0\u00072\u0006\u0010\u0019\u001a\u00020\u000fJ\u0016\u0010\u001c\u001a\u00020\u000f2\u0006\u0010\u001d\u001a\u00020\u001bH\u0086@\u00a2\u0006\u0002\u0010\u001eJ\u0016\u0010\u001f\u001a\u00020\u00132\u0006\u0010\u001d\u001a\u00020\u001bH\u0086@\u00a2\u0006\u0002\u0010\u001eJ\u0016\u0010 \u001a\u00020\u00132\u0006\u0010\u001d\u001a\u00020\u001bH\u0086@\u00a2\u0006\u0002\u0010\u001eJ\u0018\u0010!\u001a\u0004\u0018\u00010\u001b2\u0006\u0010\u0019\u001a\u00020\u000fH\u0086@\u00a2\u0006\u0002\u0010\u0017J\u001e\u0010\"\u001a\u00020\u00132\u0006\u0010\u0019\u001a\u00020\u000f2\u0006\u0010#\u001a\u00020$H\u0086@\u00a2\u0006\u0002\u0010%J\u0018\u0010&\u001a\u0004\u0018\u00010\'2\u0006\u0010\u0019\u001a\u00020\u000fH\u0086@\u00a2\u0006\u0002\u0010\u0017J$\u0010(\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\'0\b0\u00072\u0006\u0010\u0019\u001a\u00020\u000f2\b\b\u0002\u0010)\u001a\u00020*R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0006\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0019\u0010\f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000b\u00a8\u0006+"}, d2 = {"Lcom/cropdoc/app/data/repository/FarmRepository;", "", "dao", "Lcom/cropdoc/app/data/db/FarmDao;", "<init>", "(Lcom/cropdoc/app/data/db/FarmDao;)V", "allZones", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/cropdoc/app/data/model/FarmZone;", "getAllZones", "()Lkotlinx/coroutines/flow/Flow;", "activeZone", "getActiveZone", "addZone", "", "zone", "(Lcom/cropdoc/app/data/model/FarmZone;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateZone", "", "deleteZone", "getZoneById", "id", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setActiveZone", "zoneId", "getCropsForZone", "Lcom/cropdoc/app/data/model/Crop;", "addCrop", "crop", "(Lcom/cropdoc/app/data/model/Crop;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateCrop", "deleteCrop", "getLatestCropForZone", "saveReading", "reading", "Lcom/cropdoc/app/data/model/SoilReading;", "(JLcom/cropdoc/app/data/model/SoilReading;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getLatestReadingForZone", "Lcom/cropdoc/app/data/model/SoilReadingHistory;", "getReadingHistoryForZone", "limit", "", "app_debug"})
public final class FarmRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.cropdoc.app.data.db.FarmDao dao = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.cropdoc.app.data.model.FarmZone>> allZones = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<com.cropdoc.app.data.model.FarmZone> activeZone = null;
    
    public FarmRepository(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.data.db.FarmDao dao) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.cropdoc.app.data.model.FarmZone>> getAllZones() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.cropdoc.app.data.model.FarmZone> getActiveZone() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addZone(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.data.model.FarmZone zone, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateZone(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.data.model.FarmZone zone, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteZone(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.data.model.FarmZone zone, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getZoneById(long id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.cropdoc.app.data.model.FarmZone> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setActiveZone(long zoneId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.cropdoc.app.data.model.Crop>> getCropsForZone(long zoneId) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addCrop(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.data.model.Crop crop, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateCrop(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.data.model.Crop crop, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteCrop(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.data.model.Crop crop, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getLatestCropForZone(long zoneId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.cropdoc.app.data.model.Crop> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object saveReading(long zoneId, @org.jetbrains.annotations.NotNull()
    com.cropdoc.app.data.model.SoilReading reading, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getLatestReadingForZone(long zoneId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.cropdoc.app.data.model.SoilReadingHistory> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.cropdoc.app.data.model.SoilReadingHistory>> getReadingHistoryForZone(long zoneId, int limit) {
        return null;
    }
}