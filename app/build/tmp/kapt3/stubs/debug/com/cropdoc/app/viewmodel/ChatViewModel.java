package com.cropdoc.app.viewmodel;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000|\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0011\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010-\u001a\u00020.J\u000e\u0010/\u001a\u00020.2\u0006\u00100\u001a\u00020\u0016J\u0010\u00101\u001a\u00020.2\b\u00102\u001a\u0004\u0018\u00010*J\u0017\u00103\u001a\u00020.2\b\u00104\u001a\u0004\u0018\u00010\u0010H\u0002\u00a2\u0006\u0002\u00105J\u000e\u00106\u001a\u00020.2\u0006\u00107\u001a\u00020&J\u0006\u00108\u001a\u00020.J\u000e\u00109\u001a\u00020.2\u0006\u0010:\u001a\u00020\"J\u0006\u0010;\u001a\u00020.J\u000e\u0010<\u001a\u00020\"H\u0082@\u00a2\u0006\u0002\u0010=J\b\u0010>\u001a\u00020.H\u0014R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000e\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00100\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u0011\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00100\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0016\u0010\u0015\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00160\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u0017\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00160\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0014R\u001a\u0010\u0019\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001b0\u001a0\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u001c\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001b0\u001a0\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0014R\u0014\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u001f0\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010 \u001a\b\u0012\u0004\u0012\u00020\u001f0\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u0014R\u0014\u0010!\u001a\b\u0012\u0004\u0012\u00020\"0\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010#\u001a\b\u0012\u0004\u0012\u00020\"0\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u0014R\u0016\u0010%\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010&0\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\'\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010&0\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010\u0014R\u0010\u0010)\u001a\u0004\u0018\u00010*X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010+\u001a\u0004\u0018\u00010,X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006?"}, d2 = {"Lcom/cropdoc/app/viewmodel/ChatViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "<init>", "(Landroid/app/Application;)V", "chatRepository", "Lcom/cropdoc/app/data/repository/ChatRepository;", "farmRepository", "Lcom/cropdoc/app/data/repository/FarmRepository;", "weatherRepository", "Lcom/cropdoc/app/data/repository/WeatherRepository;", "aiEngine", "Lcom/cropdoc/app/data/model/CropDocAiEngine;", "_currentZoneId", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "currentZoneId", "Lkotlinx/coroutines/flow/StateFlow;", "getCurrentZoneId", "()Lkotlinx/coroutines/flow/StateFlow;", "_currentZone", "Lcom/cropdoc/app/data/model/FarmZone;", "currentZone", "getCurrentZone", "_messages", "", "Lcom/cropdoc/app/data/model/ChatMessage;", "messages", "getMessages", "_isTyping", "", "isTyping", "_streamingText", "", "streamingText", "getStreamingText", "_attachedImageUri", "Landroid/net/Uri;", "attachedImageUri", "getAttachedImageUri", "currentSoilReading", "Lcom/cropdoc/app/data/model/SoilReading;", "currentWeather", "Lcom/cropdoc/app/data/model/WeatherData;", "openGeneralChat", "", "openZoneChat", "zone", "updateSoilReading", "soilReading", "loadMessages", "zoneId", "(Ljava/lang/Long;)V", "attachImage", "uri", "clearAttachment", "sendMessage", "text", "clearChat", "buildContextSnapshot", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "onCleared", "app_debug"})
public final class ChatViewModel extends androidx.lifecycle.AndroidViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.cropdoc.app.data.repository.ChatRepository chatRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.cropdoc.app.data.repository.FarmRepository farmRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.cropdoc.app.data.repository.WeatherRepository weatherRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.cropdoc.app.data.model.CropDocAiEngine aiEngine = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Long> _currentZoneId = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Long> currentZoneId = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.cropdoc.app.data.model.FarmZone> _currentZone = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.cropdoc.app.data.model.FarmZone> currentZone = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<com.cropdoc.app.data.model.ChatMessage>> _messages = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.cropdoc.app.data.model.ChatMessage>> messages = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isTyping = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isTyping = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _streamingText = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> streamingText = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<android.net.Uri> _attachedImageUri = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<android.net.Uri> attachedImageUri = null;
    @org.jetbrains.annotations.Nullable()
    private com.cropdoc.app.data.model.SoilReading currentSoilReading;
    @org.jetbrains.annotations.Nullable()
    private com.cropdoc.app.data.model.WeatherData currentWeather;
    
    public ChatViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application application) {
        super(null);
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Long> getCurrentZoneId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.cropdoc.app.data.model.FarmZone> getCurrentZone() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.cropdoc.app.data.model.ChatMessage>> getMessages() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isTyping() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getStreamingText() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<android.net.Uri> getAttachedImageUri() {
        return null;
    }
    
    public final void openGeneralChat() {
    }
    
    public final void openZoneChat(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.data.model.FarmZone zone) {
    }
    
    public final void updateSoilReading(@org.jetbrains.annotations.Nullable()
    com.cropdoc.app.data.model.SoilReading soilReading) {
    }
    
    private final void loadMessages(java.lang.Long zoneId) {
    }
    
    public final void attachImage(@org.jetbrains.annotations.NotNull()
    android.net.Uri uri) {
    }
    
    public final void clearAttachment() {
    }
    
    public final void sendMessage(@org.jetbrains.annotations.NotNull()
    java.lang.String text) {
    }
    
    public final void clearChat() {
    }
    
    private final java.lang.Object buildContextSnapshot(kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    @java.lang.Override()
    protected void onCleared() {
    }
}