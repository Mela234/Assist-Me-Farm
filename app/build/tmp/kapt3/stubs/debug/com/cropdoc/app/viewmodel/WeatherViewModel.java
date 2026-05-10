package com.cropdoc.app.viewmodel;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\u0018\u00002\u00020\u0001:\u0001\u001eB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0016\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001aJ\u0006\u0010\u001c\u001a\u00020\u0018J\u0006\u0010\u001d\u001a\u00020\u0018R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0019\u0010\r\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000e0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\fR\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00120\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016\u00a8\u0006\u001f"}, d2 = {"Lcom/cropdoc/app/viewmodel/WeatherViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "<init>", "(Landroid/app/Application;)V", "repository", "Lcom/cropdoc/app/data/repository/WeatherRepository;", "latestWeather", "Lkotlinx/coroutines/flow/Flow;", "Lcom/cropdoc/app/data/model/WeatherData;", "getLatestWeather", "()Lkotlinx/coroutines/flow/Flow;", "weatherProfile", "Lcom/cropdoc/app/data/model/WeatherProfile;", "getWeatherProfile", "_saveState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/cropdoc/app/viewmodel/WeatherViewModel$SaveState;", "saveState", "Lkotlinx/coroutines/flow/StateFlow;", "getSaveState", "()Lkotlinx/coroutines/flow/StateFlow;", "saveProfile", "", "phoneNumber", "", "location", "optOut", "resetSaveState", "SaveState", "app_debug"})
public final class WeatherViewModel extends androidx.lifecycle.AndroidViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.cropdoc.app.data.repository.WeatherRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<com.cropdoc.app.data.model.WeatherData> latestWeather = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<com.cropdoc.app.data.model.WeatherProfile> weatherProfile = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.cropdoc.app.viewmodel.WeatherViewModel.SaveState> _saveState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.cropdoc.app.viewmodel.WeatherViewModel.SaveState> saveState = null;
    
    public WeatherViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application application) {
        super(null);
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.cropdoc.app.data.model.WeatherData> getLatestWeather() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.cropdoc.app.data.model.WeatherProfile> getWeatherProfile() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.cropdoc.app.viewmodel.WeatherViewModel.SaveState> getSaveState() {
        return null;
    }
    
    public final void saveProfile(@org.jetbrains.annotations.NotNull()
    java.lang.String phoneNumber, @org.jetbrains.annotations.NotNull()
    java.lang.String location) {
    }
    
    public final void optOut() {
    }
    
    public final void resetSaveState() {
    }
    
    @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\u0004\u0004\u0005\u0006\u0007B\t\b\u0004\u00a2\u0006\u0004\b\u0002\u0010\u0003\u0082\u0001\u0004\b\t\n\u000b\u00a8\u0006\f"}, d2 = {"Lcom/cropdoc/app/viewmodel/WeatherViewModel$SaveState;", "", "<init>", "()V", "Idle", "Saving", "Success", "Error", "Lcom/cropdoc/app/viewmodel/WeatherViewModel$SaveState$Error;", "Lcom/cropdoc/app/viewmodel/WeatherViewModel$SaveState$Idle;", "Lcom/cropdoc/app/viewmodel/WeatherViewModel$SaveState$Saving;", "Lcom/cropdoc/app/viewmodel/WeatherViewModel$SaveState$Success;", "app_debug"})
    public static abstract class SaveState {
        
        private SaveState() {
            super();
        }
        
        @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0014\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u00d6\u0083\u0004J\n\u0010\u000e\u001a\u00020\u000fH\u00d6\u0081\u0004J\n\u0010\u0010\u001a\u00020\u0003H\u00d6\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0011"}, d2 = {"Lcom/cropdoc/app/viewmodel/WeatherViewModel$SaveState$Error;", "Lcom/cropdoc/app/viewmodel/WeatherViewModel$SaveState;", "message", "", "<init>", "(Ljava/lang/String;)V", "getMessage", "()Ljava/lang/String;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "app_debug"})
        public static final class Error extends com.cropdoc.app.viewmodel.WeatherViewModel.SaveState {
            @org.jetbrains.annotations.NotNull()
            private final java.lang.String message = null;
            
            public Error(@org.jetbrains.annotations.NotNull()
            java.lang.String message) {
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String getMessage() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final java.lang.String component1() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.cropdoc.app.viewmodel.WeatherViewModel.SaveState.Error copy(@org.jetbrains.annotations.NotNull()
            java.lang.String message) {
                return null;
            }
            
            @java.lang.Override()
            public boolean equals(@org.jetbrains.annotations.Nullable()
            java.lang.Object other) {
                return false;
            }
            
            @java.lang.Override()
            public int hashCode() {
                return 0;
            }
            
            @java.lang.Override()
            @org.jetbrains.annotations.NotNull()
            public java.lang.String toString() {
                return null;
            }
        }
        
        @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/cropdoc/app/viewmodel/WeatherViewModel$SaveState$Idle;", "Lcom/cropdoc/app/viewmodel/WeatherViewModel$SaveState;", "<init>", "()V", "app_debug"})
        public static final class Idle extends com.cropdoc.app.viewmodel.WeatherViewModel.SaveState {
            @org.jetbrains.annotations.NotNull()
            public static final com.cropdoc.app.viewmodel.WeatherViewModel.SaveState.Idle INSTANCE = null;
            
            private Idle() {
            }
        }
        
        @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/cropdoc/app/viewmodel/WeatherViewModel$SaveState$Saving;", "Lcom/cropdoc/app/viewmodel/WeatherViewModel$SaveState;", "<init>", "()V", "app_debug"})
        public static final class Saving extends com.cropdoc.app.viewmodel.WeatherViewModel.SaveState {
            @org.jetbrains.annotations.NotNull()
            public static final com.cropdoc.app.viewmodel.WeatherViewModel.SaveState.Saving INSTANCE = null;
            
            private Saving() {
            }
        }
        
        @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lcom/cropdoc/app/viewmodel/WeatherViewModel$SaveState$Success;", "Lcom/cropdoc/app/viewmodel/WeatherViewModel$SaveState;", "<init>", "()V", "app_debug"})
        public static final class Success extends com.cropdoc.app.viewmodel.WeatherViewModel.SaveState {
            @org.jetbrains.annotations.NotNull()
            public static final com.cropdoc.app.viewmodel.WeatherViewModel.SaveState.Success INSTANCE = null;
            
            private Success() {
            }
        }
    }
}