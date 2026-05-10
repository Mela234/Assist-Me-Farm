package com.cropdoc.app.data.db;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0010\u0010\u0007\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050\bH\'J\u001e\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00050\n2\b\b\u0002\u0010\u000b\u001a\u00020\fH\u00a7@\u00a2\u0006\u0002\u0010\rJ\u0016\u0010\u000e\u001a\u00020\u00032\u0006\u0010\u000f\u001a\u00020\u0010H\u00a7@\u00a2\u0006\u0002\u0010\u0011J\u0016\u0010\u0012\u001a\u00020\u00032\u0006\u0010\u0013\u001a\u00020\u0014H\u00a7@\u00a2\u0006\u0002\u0010\u0015J\u0010\u0010\u0016\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00140\bH\'J\u000e\u0010\u0017\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0018\u00a8\u0006\u0019\u00c0\u0006\u0003"}, d2 = {"Lcom/cropdoc/app/data/db/WeatherDao;", "", "insertWeather", "", "weather", "Lcom/cropdoc/app/data/model/WeatherData;", "(Lcom/cropdoc/app/data/model/WeatherData;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getLatestWeather", "Lkotlinx/coroutines/flow/Flow;", "getRecentWeather", "", "limit", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteOldWeather", "cutoff", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "saveProfile", "profile", "Lcom/cropdoc/app/data/model/WeatherProfile;", "(Lcom/cropdoc/app/data/model/WeatherProfile;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getProfile", "deleteProfile", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface WeatherDao {
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertWeather(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.data.model.WeatherData weather, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM weather_data ORDER BY timestamp DESC LIMIT 1")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.cropdoc.app.data.model.WeatherData> getLatestWeather();
    
    @androidx.room.Query(value = "SELECT * FROM weather_data ORDER BY timestamp DESC LIMIT :limit")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getRecentWeather(int limit, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.cropdoc.app.data.model.WeatherData>> $completion);
    
    @androidx.room.Query(value = "DELETE FROM weather_data WHERE timestamp < :cutoff")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteOldWeather(long cutoff, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object saveProfile(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.data.model.WeatherProfile profile, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM weather_profile WHERE id = 1")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.cropdoc.app.data.model.WeatherProfile> getProfile();
    
    @androidx.room.Query(value = "DELETE FROM weather_profile")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteProfile(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @kotlin.Metadata(mv = {2, 3, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}