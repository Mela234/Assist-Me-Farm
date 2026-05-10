package com.cropdoc.app;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\u0018\u0000 \u001b2\u00020\u0001:\u0001\u001bB\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0019\u001a\u00020\u001aH\u0016R\u001b\u0010\u0004\u001a\u00020\u00058FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007R\u001b\u0010\n\u001a\u00020\u000b8FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\u000e\u0010\t\u001a\u0004\b\f\u0010\rR\u001b\u0010\u000f\u001a\u00020\u00108FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0013\u0010\t\u001a\u0004\b\u0011\u0010\u0012R\u001b\u0010\u0014\u001a\u00020\u00158FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0018\u0010\t\u001a\u0004\b\u0016\u0010\u0017\u00a8\u0006\u001c"}, d2 = {"Lcom/cropdoc/app/CropDocApplication;", "Landroid/app/Application;", "<init>", "()V", "database", "Lcom/cropdoc/app/data/db/CropDocDatabase;", "getDatabase", "()Lcom/cropdoc/app/data/db/CropDocDatabase;", "database$delegate", "Lkotlin/Lazy;", "weatherRepository", "Lcom/cropdoc/app/data/repository/WeatherRepository;", "getWeatherRepository", "()Lcom/cropdoc/app/data/repository/WeatherRepository;", "weatherRepository$delegate", "farmRepository", "Lcom/cropdoc/app/data/repository/FarmRepository;", "getFarmRepository", "()Lcom/cropdoc/app/data/repository/FarmRepository;", "farmRepository$delegate", "chatRepository", "Lcom/cropdoc/app/data/repository/ChatRepository;", "getChatRepository", "()Lcom/cropdoc/app/data/repository/ChatRepository;", "chatRepository$delegate", "onCreate", "", "Companion", "app_debug"})
public final class CropDocApplication extends android.app.Application {
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy database$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy weatherRepository$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy farmRepository$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy chatRepository$delegate = null;
    private static com.cropdoc.app.CropDocApplication instance;
    @org.jetbrains.annotations.NotNull()
    public static final com.cropdoc.app.CropDocApplication.Companion Companion = null;
    
    public CropDocApplication() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.cropdoc.app.data.db.CropDocDatabase getDatabase() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.cropdoc.app.data.repository.WeatherRepository getWeatherRepository() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.cropdoc.app.data.repository.FarmRepository getFarmRepository() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.cropdoc.app.data.repository.ChatRepository getChatRepository() {
        return null;
    }
    
    @java.lang.Override()
    public void onCreate() {
    }
    
    @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u001e\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0004\u001a\u00020\u0005@BX\u0086.\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b\u00a8\u0006\t"}, d2 = {"Lcom/cropdoc/app/CropDocApplication$Companion;", "", "<init>", "()V", "value", "Lcom/cropdoc/app/CropDocApplication;", "instance", "getInstance", "()Lcom/cropdoc/app/CropDocApplication;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.cropdoc.app.CropDocApplication getInstance() {
            return null;
        }
    }
}