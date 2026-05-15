package com.cropdoc.app.ui.screens;

@kotlin.Metadata(mv = {2, 3, 0}, k = 2, xi = 48, d1 = {"\u00000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a\u001e\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0007\u001a\u0015\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0002\u00a2\u0006\u0002\u0010\n\u001a6\u0010\u000b\u001a\u00020\u00012\u0006\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000f2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u001a\b\u0010\u0012\u001a\u00020\u0001H\u0003\u00a8\u0006\u0013"}, d2 = {"SensorScreen", "", "viewModel", "Lcom/cropdoc/app/viewmodel/CropDocViewModel;", "onNavigateBack", "Lkotlin/Function0;", "signalColor", "Landroidx/compose/ui/graphics/Color;", "rssi", "", "(I)J", "MockSensorCard", "isActive", "", "soilReading", "Lcom/cropdoc/app/data/model/SoilReading;", "onEnable", "onDisable", "SensorSetupGuide", "app_debug"})
public final class SensorScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void SensorScreen(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.viewmodel.CropDocViewModel viewModel, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateBack) {
    }
    
    private static final long signalColor(int rssi) {
        return 0L;
    }
    
    @androidx.compose.runtime.Composable()
    private static final void MockSensorCard(boolean isActive, com.cropdoc.app.data.model.SoilReading soilReading, kotlin.jvm.functions.Function0<kotlin.Unit> onEnable, kotlin.jvm.functions.Function0<kotlin.Unit> onDisable) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void SensorSetupGuide() {
    }
}