package com.cropdoc.app.ui.screens;

@kotlin.Metadata(mv = {2, 3, 0}, k = 2, xi = 48, d1 = {"\u0000L\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u001e\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0007\u001a\u0010\u0010\u0006\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\bH\u0003\u001a\u0010\u0010\t\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u000bH\u0003\u001a6\u0010\f\u001a\u00020\u00012\b\b\u0002\u0010\r\u001a\u00020\u000e2\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00010\u00102\u000e\b\u0002\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u001a6\u0010\u0013\u001a\u00020\u00012\u0006\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u00112\u0006\u0010\u0017\u001a\u00020\u00182\u0012\u0010\u0019\u001a\u000e\u0012\u0004\u0012\u00020\u001a\u0012\u0004\u0012\u00020\u00010\u0010H\u0002\u00a8\u0006\u001b"}, d2 = {"CameraScreen", "", "viewModel", "Lcom/cropdoc/app/viewmodel/CropDocViewModel;", "onNavigateBack", "Lkotlin/Function0;", "AnalysingCard", "streamingText", "", "AnalysisResultCard", "result", "Lcom/cropdoc/app/data/model/AnalysisResult;", "CameraPreview", "modifier", "Landroidx/compose/ui/Modifier;", "onImageCaptureReady", "Lkotlin/Function1;", "Landroidx/camera/core/ImageCapture;", "onCameraError", "captureImage", "context", "Landroid/content/Context;", "imageCapture", "executor", "Ljava/util/concurrent/Executor;", "onImageCaptured", "Landroid/net/Uri;", "app_debug"})
public final class CameraScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void CameraScreen(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.viewmodel.CropDocViewModel viewModel, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateBack) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void AnalysingCard(java.lang.String streamingText) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void AnalysisResultCard(com.cropdoc.app.data.model.AnalysisResult result) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void CameraPreview(androidx.compose.ui.Modifier modifier, kotlin.jvm.functions.Function1<? super androidx.camera.core.ImageCapture, kotlin.Unit> onImageCaptureReady, kotlin.jvm.functions.Function0<kotlin.Unit> onCameraError) {
    }
    
    private static final void captureImage(android.content.Context context, androidx.camera.core.ImageCapture imageCapture, java.util.concurrent.Executor executor, kotlin.jvm.functions.Function1<? super android.net.Uri, kotlin.Unit> onImageCaptured) {
    }
}