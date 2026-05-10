package com.cropdoc.app.viewmodel;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\u00b8\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0015\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\b\u0010N\u001a\u00020OH\u0002J\u0006\u0010P\u001a\u00020OJ\u0006\u0010Q\u001a\u00020OJ\u0006\u0010R\u001a\u00020OJ\u000e\u0010S\u001a\u00020O2\u0006\u0010T\u001a\u000200J\u0006\u0010U\u001a\u00020OJ\u0010\u0010V\u001a\u00020O2\b\b\u0002\u0010W\u001a\u00020\"J\u0006\u0010X\u001a\u00020OJ\u0006\u0010Y\u001a\u00020OJ\u0006\u0010Z\u001a\u00020OJ\u0006\u0010[\u001a\u00020OJ\u0006\u0010\\\u001a\u00020OJ\u0006\u0010]\u001a\u00020OJ\u000e\u0010^\u001a\u00020O2\u0006\u0010_\u001a\u00020,J\u0006\u0010`\u001a\u00020OJ\u000e\u0010a\u001a\u00020O2\u0006\u0010b\u001a\u00020,J\u000e\u0010c\u001a\u00020O2\u0006\u0010d\u001a\u00020eJ\u0012\u0010f\u001a\u0004\u0018\u00010g2\u0006\u0010T\u001a\u000200H\u0002J\b\u0010h\u001a\u00020OH\u0014R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u000e\u0010\f\u001a\u00020\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0017\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00160\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0014R\u001d\u0010\u0018\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001a0\u00190\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0014R\u0016\u0010\u001c\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u001e0\u001dX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u001f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u001e0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u0014R\u0014\u0010!\u001a\b\u0012\u0004\u0012\u00020\"0\u001dX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010#\u001a\b\u0012\u0004\u0012\u00020\"0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u0014R\u0010\u0010%\u001a\u0004\u0018\u00010&X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\'\u001a\b\u0012\u0004\u0012\u00020(0\u001dX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010)\u001a\b\u0012\u0004\u0012\u00020(0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b*\u0010\u0014R\u0014\u0010+\u001a\b\u0012\u0004\u0012\u00020,0\u001dX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010-\u001a\b\u0012\u0004\u0012\u00020,0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b.\u0010\u0014R\u0016\u0010/\u001a\n\u0012\u0006\u0012\u0004\u0018\u0001000\u001dX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u00101\u001a\n\u0012\u0006\u0012\u0004\u0018\u0001000\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b2\u0010\u0014R\u001a\u00103\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002040\u00190\u001dX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u00105\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002040\u00190\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b6\u0010\u0014R\u0016\u00107\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010,0\u001dX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u00108\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010,0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b9\u0010\u0014R\u0014\u0010:\u001a\b\u0012\u0004\u0012\u00020\"0\u001dX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010;\u001a\b\u0012\u0004\u0012\u00020\"0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b<\u0010\u0014R\u0014\u0010=\u001a\b\u0012\u0004\u0012\u00020,0\u001dX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010>\u001a\b\u0012\u0004\u0012\u00020,0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b?\u0010\u0014R\u0019\u0010@\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010B0A\u00a2\u0006\b\n\u0000\u001a\u0004\bC\u0010DR\u0019\u0010E\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010F0A\u00a2\u0006\b\n\u0000\u001a\u0004\bG\u0010DR\u001d\u0010H\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020I0\u00190A\u00a2\u0006\b\n\u0000\u001a\u0004\bJ\u0010DR\u0016\u0010K\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010I0\u001dX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010L\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010I0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\bM\u0010\u0014\u00a8\u0006i"}, d2 = {"Lcom/cropdoc/app/viewmodel/CropDocViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "<init>", "(Landroid/app/Application;)V", "aiEngine", "Lcom/cropdoc/app/data/model/CropDocAiEngine;", "bleManager", "Lcom/cropdoc/app/data/ble/SoilSensorBleManager;", "getBleManager", "()Lcom/cropdoc/app/data/ble/SoilSensorBleManager;", "farmRepository", "Lcom/cropdoc/app/data/repository/FarmRepository;", "weatherRepository", "Lcom/cropdoc/app/data/repository/WeatherRepository;", "modelState", "Lkotlinx/coroutines/flow/StateFlow;", "Lcom/cropdoc/app/data/model/ModelState;", "getModelState", "()Lkotlinx/coroutines/flow/StateFlow;", "bleState", "Lcom/cropdoc/app/data/model/BleState;", "getBleState", "scannedDevices", "", "Lcom/cropdoc/app/data/model/BleDevice;", "getScannedDevices", "_soilReading", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/cropdoc/app/data/model/SoilReading;", "soilReading", "getSoilReading", "_mockSensorActive", "", "mockSensorActive", "getMockSensorActive", "mockSensorJob", "Lkotlinx/coroutines/Job;", "_analysisState", "Lcom/cropdoc/app/data/model/AnalysisState;", "analysisState", "getAnalysisState", "_streamingText", "", "streamingText", "getStreamingText", "_capturedImageUri", "Landroid/net/Uri;", "capturedImageUri", "getCapturedImageUri", "_analysisHistory", "Lcom/cropdoc/app/data/model/AnalysisResult;", "analysisHistory", "getAnalysisHistory", "_soilSummary", "soilSummary", "getSoilSummary", "_soilSummaryLoading", "soilSummaryLoading", "getSoilSummaryLoading", "_currentLanguage", "currentLanguage", "getCurrentLanguage", "latestWeather", "Lkotlinx/coroutines/flow/Flow;", "Lcom/cropdoc/app/data/model/WeatherData;", "getLatestWeather", "()Lkotlinx/coroutines/flow/Flow;", "weatherProfile", "Lcom/cropdoc/app/data/model/WeatherProfile;", "getWeatherProfile", "allZones", "Lcom/cropdoc/app/data/model/FarmZone;", "getAllZones", "_activeZoneState", "activeZone", "getActiveZone", "initEngine", "", "retryEngineLoad", "enableMockSensor", "disableMockSensor", "onImageCaptured", "uri", "clearImage", "analyseCapture", "includeSoil", "analyseSoilOnly", "summariseSoil", "clearSoilSummary", "resetAnalysis", "startBleScan", "stopBleScan", "connectToSensor", "address", "disconnectSensor", "setLanguage", "code", "setActiveZone", "zoneId", "", "loadBitmapFromUri", "Landroid/graphics/Bitmap;", "onCleared", "app_debug"})
public final class CropDocViewModel extends androidx.lifecycle.AndroidViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.cropdoc.app.data.model.CropDocAiEngine aiEngine = null;
    @org.jetbrains.annotations.NotNull()
    private final com.cropdoc.app.data.ble.SoilSensorBleManager bleManager = null;
    @org.jetbrains.annotations.NotNull()
    private final com.cropdoc.app.data.repository.FarmRepository farmRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.cropdoc.app.data.repository.WeatherRepository weatherRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.cropdoc.app.data.model.ModelState> modelState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.cropdoc.app.data.model.BleState> bleState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.cropdoc.app.data.model.BleDevice>> scannedDevices = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.cropdoc.app.data.model.SoilReading> _soilReading = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.cropdoc.app.data.model.SoilReading> soilReading = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _mockSensorActive = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> mockSensorActive = null;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job mockSensorJob;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.cropdoc.app.data.model.AnalysisState> _analysisState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.cropdoc.app.data.model.AnalysisState> analysisState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _streamingText = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> streamingText = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<android.net.Uri> _capturedImageUri = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<android.net.Uri> capturedImageUri = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<com.cropdoc.app.data.model.AnalysisResult>> _analysisHistory = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.cropdoc.app.data.model.AnalysisResult>> analysisHistory = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _soilSummary = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> soilSummary = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _soilSummaryLoading = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> soilSummaryLoading = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _currentLanguage = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> currentLanguage = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<com.cropdoc.app.data.model.WeatherData> latestWeather = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<com.cropdoc.app.data.model.WeatherProfile> weatherProfile = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.cropdoc.app.data.model.FarmZone>> allZones = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.cropdoc.app.data.model.FarmZone> _activeZoneState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.cropdoc.app.data.model.FarmZone> activeZone = null;
    
    public CropDocViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application application) {
        super(null);
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.cropdoc.app.data.ble.SoilSensorBleManager getBleManager() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.cropdoc.app.data.model.ModelState> getModelState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.cropdoc.app.data.model.BleState> getBleState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.cropdoc.app.data.model.BleDevice>> getScannedDevices() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.cropdoc.app.data.model.SoilReading> getSoilReading() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> getMockSensorActive() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.cropdoc.app.data.model.AnalysisState> getAnalysisState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getStreamingText() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<android.net.Uri> getCapturedImageUri() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.cropdoc.app.data.model.AnalysisResult>> getAnalysisHistory() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getSoilSummary() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> getSoilSummaryLoading() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getCurrentLanguage() {
        return null;
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
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.cropdoc.app.data.model.FarmZone>> getAllZones() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.cropdoc.app.data.model.FarmZone> getActiveZone() {
        return null;
    }
    
    private final void initEngine() {
    }
    
    public final void retryEngineLoad() {
    }
    
    public final void enableMockSensor() {
    }
    
    public final void disableMockSensor() {
    }
    
    public final void onImageCaptured(@org.jetbrains.annotations.NotNull()
    android.net.Uri uri) {
    }
    
    public final void clearImage() {
    }
    
    public final void analyseCapture(boolean includeSoil) {
    }
    
    public final void analyseSoilOnly() {
    }
    
    public final void summariseSoil() {
    }
    
    public final void clearSoilSummary() {
    }
    
    public final void resetAnalysis() {
    }
    
    public final void startBleScan() {
    }
    
    public final void stopBleScan() {
    }
    
    public final void connectToSensor(@org.jetbrains.annotations.NotNull()
    java.lang.String address) {
    }
    
    public final void disconnectSensor() {
    }
    
    public final void setLanguage(@org.jetbrains.annotations.NotNull()
    java.lang.String code) {
    }
    
    public final void setActiveZone(long zoneId) {
    }
    
    private final android.graphics.Bitmap loadBitmapFromUri(android.net.Uri uri) {
        return null;
    }
    
    @java.lang.Override()
    protected void onCleared() {
    }
}