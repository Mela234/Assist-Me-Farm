package com.cropdoc.app.data.model;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0014\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001BK\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n\u0012\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\n0\u0003\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u00a2\u0006\u0004\b\u000e\u0010\u000fJ\u000f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\u000f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\bH\u00c6\u0003J\t\u0010\u001d\u001a\u00020\nH\u00c6\u0003J\u000f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\n0\u0003H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\rH\u00c6\u0003JW\u0010 \u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u00032\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\n2\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\n0\u00032\b\b\u0002\u0010\f\u001a\u00020\rH\u00c6\u0001J\u0014\u0010!\u001a\u00020\"2\b\u0010#\u001a\u0004\u0018\u00010\u0001H\u00d6\u0083\u0004J\n\u0010$\u001a\u00020\bH\u00d6\u0081\u0004J\n\u0010%\u001a\u00020\nH\u00d6\u0081\u0004R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0011R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\n0\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0011R\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019\u00a8\u0006&"}, d2 = {"Lcom/cropdoc/app/data/model/AnalysisResult;", "", "diseases", "", "Lcom/cropdoc/app/data/model/DiseaseDetection;", "soilRecommendations", "Lcom/cropdoc/app/data/model/SoilRecommendation;", "overallHealthScore", "", "summary", "", "immediateActions", "timestamp", "", "<init>", "(Ljava/util/List;Ljava/util/List;ILjava/lang/String;Ljava/util/List;J)V", "getDiseases", "()Ljava/util/List;", "getSoilRecommendations", "getOverallHealthScore", "()I", "getSummary", "()Ljava/lang/String;", "getImmediateActions", "getTimestamp", "()J", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "", "other", "hashCode", "toString", "app_debug"})
public final class AnalysisResult {
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.cropdoc.app.data.model.DiseaseDetection> diseases = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.cropdoc.app.data.model.SoilRecommendation> soilRecommendations = null;
    private final int overallHealthScore = 0;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String summary = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> immediateActions = null;
    private final long timestamp = 0L;
    
    public AnalysisResult(@org.jetbrains.annotations.NotNull()
    java.util.List<com.cropdoc.app.data.model.DiseaseDetection> diseases, @org.jetbrains.annotations.NotNull()
    java.util.List<com.cropdoc.app.data.model.SoilRecommendation> soilRecommendations, int overallHealthScore, @org.jetbrains.annotations.NotNull()
    java.lang.String summary, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> immediateActions, long timestamp) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.cropdoc.app.data.model.DiseaseDetection> getDiseases() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.cropdoc.app.data.model.SoilRecommendation> getSoilRecommendations() {
        return null;
    }
    
    public final int getOverallHealthScore() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSummary() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> getImmediateActions() {
        return null;
    }
    
    public final long getTimestamp() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.cropdoc.app.data.model.DiseaseDetection> component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.cropdoc.app.data.model.SoilRecommendation> component2() {
        return null;
    }
    
    public final int component3() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<java.lang.String> component5() {
        return null;
    }
    
    public final long component6() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.cropdoc.app.data.model.AnalysisResult copy(@org.jetbrains.annotations.NotNull()
    java.util.List<com.cropdoc.app.data.model.DiseaseDetection> diseases, @org.jetbrains.annotations.NotNull()
    java.util.List<com.cropdoc.app.data.model.SoilRecommendation> soilRecommendations, int overallHealthScore, @org.jetbrains.annotations.NotNull()
    java.lang.String summary, @org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> immediateActions, long timestamp) {
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