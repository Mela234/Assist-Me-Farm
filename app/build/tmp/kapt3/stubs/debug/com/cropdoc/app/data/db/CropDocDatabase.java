package com.cropdoc.app.data.db;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\'\u0018\u0000 \n2\u00020\u0001:\u0001\nB\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H&J\b\u0010\u0006\u001a\u00020\u0007H&J\b\u0010\b\u001a\u00020\tH&\u00a8\u0006\u000b"}, d2 = {"Lcom/cropdoc/app/data/db/CropDocDatabase;", "Landroidx/room/RoomDatabase;", "<init>", "()V", "weatherDao", "Lcom/cropdoc/app/data/db/WeatherDao;", "farmDao", "Lcom/cropdoc/app/data/db/FarmDao;", "chatDao", "Lcom/cropdoc/app/data/db/ChatDao;", "Companion", "app_debug"})
@androidx.room.Database(entities = {com.cropdoc.app.data.model.WeatherData.class, com.cropdoc.app.data.model.WeatherProfile.class, com.cropdoc.app.data.model.FarmZone.class, com.cropdoc.app.data.model.Crop.class, com.cropdoc.app.data.model.SoilReadingHistory.class, com.cropdoc.app.data.model.ChatMessage.class}, version = 1, exportSchema = false)
public abstract class CropDocDatabase extends androidx.room.RoomDatabase {
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.cropdoc.app.data.db.CropDocDatabase INSTANCE;
    @org.jetbrains.annotations.NotNull()
    public static final com.cropdoc.app.data.db.CropDocDatabase.Companion Companion = null;
    
    public CropDocDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.cropdoc.app.data.db.WeatherDao weatherDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.cropdoc.app.data.db.FarmDao farmDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract com.cropdoc.app.data.db.ChatDao chatDao();
    
    @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\bR\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2 = {"Lcom/cropdoc/app/data/db/CropDocDatabase$Companion;", "", "<init>", "()V", "INSTANCE", "Lcom/cropdoc/app/data/db/CropDocDatabase;", "getInstance", "context", "Landroid/content/Context;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.cropdoc.app.data.db.CropDocDatabase getInstance(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
    }
}