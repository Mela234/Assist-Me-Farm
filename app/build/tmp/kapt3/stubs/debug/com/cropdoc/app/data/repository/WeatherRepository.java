package com.cropdoc.app.data.repository;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\b\n\u0002\u0010 \n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0016\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\u0011J\u0016\u0010\u0012\u001a\u00020\u000f2\u0006\u0010\u0013\u001a\u00020\fH\u0086@\u00a2\u0006\u0002\u0010\u0014J\u000e\u0010\u0015\u001a\u00020\u000fH\u0086@\u00a2\u0006\u0002\u0010\u0016J\u001e\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\b0\u00182\b\b\u0002\u0010\u0019\u001a\u00020\u001aH\u0086@\u00a2\u0006\u0002\u0010\u001bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u0006\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\b0\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0019\u0010\u000b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\f0\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\n\u00a8\u0006\u001c"}, d2 = {"Lcom/cropdoc/app/data/repository/WeatherRepository;", "", "dao", "Lcom/cropdoc/app/data/db/WeatherDao;", "<init>", "(Lcom/cropdoc/app/data/db/WeatherDao;)V", "latestWeather", "Lkotlinx/coroutines/flow/Flow;", "Lcom/cropdoc/app/data/model/WeatherData;", "getLatestWeather", "()Lkotlinx/coroutines/flow/Flow;", "weatherProfile", "Lcom/cropdoc/app/data/model/WeatherProfile;", "getWeatherProfile", "saveWeather", "", "weather", "(Lcom/cropdoc/app/data/model/WeatherData;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "saveProfile", "profile", "(Lcom/cropdoc/app/data/model/WeatherProfile;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteProfile", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getRecentWeather", "", "limit", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class WeatherRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.cropdoc.app.data.db.WeatherDao dao = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<com.cropdoc.app.data.model.WeatherData> latestWeather = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<com.cropdoc.app.data.model.WeatherProfile> weatherProfile = null;
    
    public WeatherRepository(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.data.db.WeatherDao dao) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.cropdoc.app.data.model.WeatherData> getLatestWeather() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.cropdoc.app.data.model.WeatherProfile> getWeatherProfile() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object saveWeather(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.data.model.WeatherData weather, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object saveProfile(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.data.model.WeatherProfile profile, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteProfile(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getRecentWeather(int limit, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.cropdoc.app.data.model.WeatherData>> $completion) {
        return null;
    }
}