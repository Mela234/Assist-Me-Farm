package com.cropdoc.app.ui.screens;

@kotlin.Metadata(mv = {2, 3, 0}, k = 2, xi = 48, d1 = {"\u0000b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u000b\n\u0002\u0010\u000e\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\u001a2\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\u0012\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\u0007H\u0007\u001aX\u0010\t\u001a\u00020\u00012\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000b2\u0012\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00010\u00072\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00010\u00072\u0018\u0010\u0010\u001a\u0014\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00010\u0011H\u0003\u001at\u0010\u0012\u001a\u00020\u00012\u0006\u0010\u0013\u001a\u00020\f2\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00152\u0006\u0010\u0017\u001a\u00020\u00152\u0006\u0010\u0018\u001a\u00020\u00152\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\u0012\u0010\u001a\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00010\u00072\u0012\u0010\u001b\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00010\u00072\f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u001a?\u0010\u001d\u001a\u00020\u00012\u0006\u0010\u001e\u001a\u00020\u000f2\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\u0018\u0010 \u001a\u0014\u0012\u0004\u0012\u00020!\u0012\u0004\u0012\u00020\"\u0012\u0004\u0012\u00020\u00010\u0011H\u0003\u00a2\u0006\u0004\b#\u0010$\u001aR\u0010%\u001a\u00020\u00012\u0006\u0010\u0013\u001a\u00020\f2\f\u0010&\u001a\b\u0012\u0004\u0012\u00020\'0\u000b2\b\u0010(\u001a\u0004\u0018\u00010)2\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\u0012\u0010*\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\u0007H\u0003\u001a\u0018\u0010+\u001a\u00020\u00012\u0006\u0010,\u001a\u00020\'2\u0006\u0010\u0002\u001a\u00020\u0003H\u0003\u001a \u0010-\u001a\u00020\u00012\u0006\u0010.\u001a\u00020!2\u0006\u0010/\u001a\u00020!2\u0006\u00100\u001a\u00020!H\u0003\u001aD\u00101\u001a\u00020\u00012\u0006\u00102\u001a\u00020\b2\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052$\u0010 \u001a \u0012\u0004\u0012\u00020!\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\"\u0012\u0004\u0012\u00020!\u0012\u0004\u0012\u00020\u000103H\u0003\u00a8\u00064"}, d2 = {"FarmMapScreen", "", "farmViewModel", "Lcom/cropdoc/app/viewmodel/FarmViewModel;", "onNavigateBack", "Lkotlin/Function0;", "onNavigateToZoneChat", "Lkotlin/Function1;", "", "FarmCanvas", "zones", "", "Lcom/cropdoc/app/data/model/FarmZone;", "onZoneTap", "onCanvasLongPress", "Landroidx/compose/ui/geometry/Offset;", "onZoneMoved", "Lkotlin/Function2;", "ZoneBlock", "zone", "x", "", "y", "width", "height", "onTap", "onDragStart", "onDrag", "onDragEnd", "AddZoneDialog", "position", "onDismiss", "onConfirm", "", "", "AddZoneDialog-9KIMszo", "(JLkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function2;)V", "ZoneDetailSheet", "crops", "Lcom/cropdoc/app/data/model/Crop;", "latestReading", "Lcom/cropdoc/app/data/model/SoilReadingHistory;", "onChatAboutZone", "CropCard", "crop", "MiniReadingStat", "emoji", "value", "label", "AddCropDialog", "zoneId", "Lkotlin/Function4;", "app_debug"})
public final class FarmMapScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void FarmMapScreen(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.viewmodel.FarmViewModel farmViewModel, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateBack, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Long, kotlin.Unit> onNavigateToZoneChat) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void FarmCanvas(java.util.List<com.cropdoc.app.data.model.FarmZone> zones, kotlin.jvm.functions.Function1<? super com.cropdoc.app.data.model.FarmZone, kotlin.Unit> onZoneTap, kotlin.jvm.functions.Function1<? super androidx.compose.ui.geometry.Offset, kotlin.Unit> onCanvasLongPress, kotlin.jvm.functions.Function2<? super com.cropdoc.app.data.model.FarmZone, ? super androidx.compose.ui.geometry.Offset, kotlin.Unit> onZoneMoved) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ZoneBlock(com.cropdoc.app.data.model.FarmZone zone, float x, float y, float width, float height, kotlin.jvm.functions.Function0<kotlin.Unit> onTap, kotlin.jvm.functions.Function1<? super androidx.compose.ui.geometry.Offset, kotlin.Unit> onDragStart, kotlin.jvm.functions.Function1<? super androidx.compose.ui.geometry.Offset, kotlin.Unit> onDrag, kotlin.jvm.functions.Function0<kotlin.Unit> onDragEnd) {
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    private static final void ZoneDetailSheet(com.cropdoc.app.data.model.FarmZone zone, java.util.List<com.cropdoc.app.data.model.Crop> crops, com.cropdoc.app.data.model.SoilReadingHistory latestReading, com.cropdoc.app.viewmodel.FarmViewModel farmViewModel, kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss, kotlin.jvm.functions.Function1<? super java.lang.Long, kotlin.Unit> onChatAboutZone) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void CropCard(com.cropdoc.app.data.model.Crop crop, com.cropdoc.app.viewmodel.FarmViewModel farmViewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void MiniReadingStat(java.lang.String emoji, java.lang.String value, java.lang.String label) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void AddCropDialog(long zoneId, kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss, kotlin.jvm.functions.Function4<? super java.lang.String, ? super java.lang.Long, ? super java.lang.Integer, ? super java.lang.String, kotlin.Unit> onConfirm) {
    }
}