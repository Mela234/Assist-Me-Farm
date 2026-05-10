package com.cropdoc.app.data.model;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\u0098\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0007\u0018\u0000 L2\u00020\u0001:\u0001LB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u000e\u0010\u0011\u001a\u00020\u0012H\u0086@\u00a2\u0006\u0002\u0010\u0013J<\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00160\u00152\u0006\u0010\u0017\u001a\u00020\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u001a2\u0012\u0010\u001b\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00120\u001cH\u0086@\u00a2\u0006\u0004\b\u001d\u0010\u001eJ2\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00160\u00152\u0006\u0010\u0019\u001a\u00020\u001a2\u0012\u0010\u001b\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00120\u001cH\u0086@\u00a2\u0006\u0004\b \u0010!J\u0016\u0010\"\u001a\u00020\u00102\u0006\u0010\u0019\u001a\u00020\u001aH\u0086@\u00a2\u0006\u0002\u0010#Jp\u0010$\u001a\b\u0012\u0004\u0012\u00020\u00100\u00152\u0006\u0010%\u001a\u00020\u00102\b\u0010&\u001a\u0004\u0018\u00010\'2\b\u0010\u0019\u001a\u0004\u0018\u00010\u001a2\b\u0010(\u001a\u0004\u0018\u00010)2\b\u0010*\u001a\u0004\u0018\u00010+2\f\u0010,\u001a\b\u0012\u0004\u0012\u00020.0-2\u0006\u0010\u0002\u001a\u00020\u00032\u0012\u0010\u001b\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00120\u001cH\u0086@\u00a2\u0006\u0004\b/\u00100J&\u00101\u001a\u00020\u00102\b\u0010\u0019\u001a\u0004\u0018\u00010\u001a2\b\u0010(\u001a\u0004\u0018\u00010)2\b\u0010*\u001a\u0004\u0018\u00010+H\u0002J.\u00102\u001a\u00020\u00102\u0006\u0010%\u001a\u00020\u00102\b\u0010\u0019\u001a\u0004\u0018\u00010\u001a2\b\u0010(\u001a\u0004\u0018\u00010)2\b\u0010*\u001a\u0004\u0018\u00010+H\u0002J\u0012\u00104\u001a\u00020\u00102\b\u0010\u0019\u001a\u0004\u0018\u00010\u001aH\u0002J\u0010\u00105\u001a\u00020\u00102\u0006\u0010\u0019\u001a\u00020\u001aH\u0002J\u001a\u00106\u001a\u00020\u00162\u0006\u00107\u001a\u00020\u00102\b\u0010\u0019\u001a\u0004\u0018\u00010\u001aH\u0002J\u0010\u00108\u001a\u0002092\u0006\u0010:\u001a\u00020\u0010H\u0002J4\u0010;\u001a\u00020\u00162\b\u0010\u0019\u001a\u0004\u0018\u00010\u001a2\u0006\u0010<\u001a\u00020=2\u0012\u0010\u001b\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00120\u001cH\u0082@\u00a2\u0006\u0002\u0010>J\u0010\u0010?\u001a\u00020\u00102\u0006\u0010@\u001a\u00020\u001aH\u0002J\u0012\u0010A\u001a\u00020B2\b\u0010\u0019\u001a\u0004\u0018\u00010\u001aH\u0002J\u0018\u0010C\u001a\b\u0012\u0004\u0012\u00020D0-2\b\u0010@\u001a\u0004\u0018\u00010\u001aH\u0002J\u0014\u0010E\u001a\u00020\u0010*\u00020F2\u0006\u0010G\u001a\u00020BH\u0002J\u000e\u0010H\u001a\u00020\u00122\u0006\u0010I\u001a\u00020\u0010J\b\u0010J\u001a\u00020\u0010H\u0002J\u0006\u0010K\u001a\u00020\u0012R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\b0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u00103\u001a\u00020\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006M"}, d2 = {"Lcom/cropdoc/app/data/model/CropDocAiEngine;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "_modelState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/cropdoc/app/data/model/ModelState;", "modelState", "Lkotlinx/coroutines/flow/StateFlow;", "getModelState", "()Lkotlinx/coroutines/flow/StateFlow;", "engine", "Lcom/google/ai/edge/litertlm/Engine;", "currentLanguage", "", "initialize", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "analyseCrop", "Lkotlin/Result;", "Lcom/cropdoc/app/data/model/AnalysisResult;", "imageBitmap", "Landroid/graphics/Bitmap;", "soilReading", "Lcom/cropdoc/app/data/model/SoilReading;", "onToken", "Lkotlin/Function1;", "analyseCrop-BWLJW6A", "(Landroid/graphics/Bitmap;Lcom/cropdoc/app/data/model/SoilReading;Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "analyseSoilOnly", "analyseSoilOnly-0E7RQCE", "(Lcom/cropdoc/app/data/model/SoilReading;Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "summariseSoil", "(Lcom/cropdoc/app/data/model/SoilReading;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "chat", "userMessage", "imageUri", "Landroid/net/Uri;", "weatherData", "Lcom/cropdoc/app/data/model/WeatherData;", "zone", "Lcom/cropdoc/app/data/model/FarmZone;", "history", "", "Lcom/cropdoc/app/data/model/ChatMessage;", "chat-tZkwj4A", "(Ljava/lang/String;Landroid/net/Uri;Lcom/cropdoc/app/data/model/SoilReading;Lcom/cropdoc/app/data/model/WeatherData;Lcom/cropdoc/app/data/model/FarmZone;Ljava/util/List;Landroid/content/Context;Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "buildChatSystemPrompt", "buildDemoChatResponse", "SYSTEM_PROMPT", "buildCropPrompt", "buildSoilOnlyPrompt", "parseJsonResponse", "response", "parseSeverity", "Lcom/cropdoc/app/data/model/SeverityLevel;", "value", "streamDemoResponse", "withImage", "", "(Lcom/cropdoc/app/data/model/SoilReading;ZLkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "buildDemoSoilSummary", "s", "estimateHealthScore", "", "buildSoilRecommendations", "Lcom/cropdoc/app/data/model/SoilRecommendation;", "fmt", "", "decimals", "setLanguage", "code", "languageInstruction", "release", "Companion", "app_debug"})
public final class CropDocAiEngine {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "CropDocAI";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String MODEL_ASSET = "gemma-4-E4B-it.litertlm";
    private static final boolean USE_DEMO_MODE = true;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.cropdoc.app.data.model.ModelState> _modelState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.cropdoc.app.data.model.ModelState> modelState = null;
    @org.jetbrains.annotations.Nullable()
    private com.google.ai.edge.litertlm.Engine engine;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String currentLanguage = "en";
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String SYSTEM_PROMPT = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.cropdoc.app.data.model.CropDocAiEngine.Companion Companion = null;
    
    public CropDocAiEngine(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.cropdoc.app.data.model.ModelState> getModelState() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object initialize(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object summariseSoil(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.data.model.SoilReading soilReading, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    private final java.lang.String buildChatSystemPrompt(com.cropdoc.app.data.model.SoilReading soilReading, com.cropdoc.app.data.model.WeatherData weatherData, com.cropdoc.app.data.model.FarmZone zone) {
        return null;
    }
    
    private final java.lang.String buildDemoChatResponse(java.lang.String userMessage, com.cropdoc.app.data.model.SoilReading soilReading, com.cropdoc.app.data.model.WeatherData weatherData, com.cropdoc.app.data.model.FarmZone zone) {
        return null;
    }
    
    private final java.lang.String buildCropPrompt(com.cropdoc.app.data.model.SoilReading soilReading) {
        return null;
    }
    
    private final java.lang.String buildSoilOnlyPrompt(com.cropdoc.app.data.model.SoilReading soilReading) {
        return null;
    }
    
    private final com.cropdoc.app.data.model.AnalysisResult parseJsonResponse(java.lang.String response, com.cropdoc.app.data.model.SoilReading soilReading) {
        return null;
    }
    
    private final com.cropdoc.app.data.model.SeverityLevel parseSeverity(java.lang.String value) {
        return null;
    }
    
    private final java.lang.Object streamDemoResponse(com.cropdoc.app.data.model.SoilReading soilReading, boolean withImage, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onToken, kotlin.coroutines.Continuation<? super com.cropdoc.app.data.model.AnalysisResult> $completion) {
        return null;
    }
    
    private final java.lang.String buildDemoSoilSummary(com.cropdoc.app.data.model.SoilReading s) {
        return null;
    }
    
    private final int estimateHealthScore(com.cropdoc.app.data.model.SoilReading soilReading) {
        return 0;
    }
    
    private final java.util.List<com.cropdoc.app.data.model.SoilRecommendation> buildSoilRecommendations(com.cropdoc.app.data.model.SoilReading s) {
        return null;
    }
    
    private final java.lang.String fmt(float $this$fmt, int decimals) {
        return null;
    }
    
    public final void setLanguage(@org.jetbrains.annotations.NotNull()
    java.lang.String code) {
    }
    
    private final java.lang.String languageInstruction() {
        return null;
    }
    
    public final void release() {
    }
    
    @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2 = {"Lcom/cropdoc/app/data/model/CropDocAiEngine$Companion;", "", "<init>", "()V", "TAG", "", "MODEL_ASSET", "USE_DEMO_MODE", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}