package com.cropdoc.app.ui.screens;

@kotlin.Metadata(mv = {2, 3, 0}, k = 2, xi = 48, d1 = {"\u0000d\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a\u0082\u0001\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00010\t2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\t2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00010\t2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00010\t2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00010\t2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00010\tH\u0007\u001a(\u0010\u0010\u001a\u00020\u00012\b\u0010\u0011\u001a\u0004\u0018\u00010\u00122\u0006\u0010\u0013\u001a\u00020\u00142\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00010\tH\u0003\u001a \u0010\u0016\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u00182\u0006\u0010\u001a\u001a\u00020\u0018H\u0003\u001a$\u0010\u001b\u001a\u00020\u00012\f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001e0\u001d2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00010\tH\u0003\u001a\u0016\u0010\u001f\u001a\u00020\u00012\f\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00010\tH\u0003\u001a\u001e\u0010!\u001a\u00020\u00012\u0006\u0010\"\u001a\u00020#2\f\u0010$\u001a\b\u0012\u0004\u0012\u00020\u00010\tH\u0003\u001aO\u0010%\u001a\u00020\u00012\u0006\u0010&\u001a\u00020\'2\u0006\u0010(\u001a\u00020\u00182\u0006\u0010)\u001a\u00020\u00182\u0006\u0010*\u001a\u00020+2\u0006\u0010,\u001a\u00020\u00142\b\b\u0002\u0010-\u001a\u00020.2\f\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00010\tH\u0003\u00a2\u0006\u0004\b/\u00100\u001a\b\u00101\u001a\u00020\u0001H\u0003\u00a8\u00062"}, d2 = {"HomeScreen", "", "viewModel", "Lcom/cropdoc/app/viewmodel/CropDocViewModel;", "weatherViewModel", "Lcom/cropdoc/app/viewmodel/WeatherViewModel;", "farmViewModel", "Lcom/cropdoc/app/viewmodel/FarmViewModel;", "onNavigateToCamera", "Lkotlin/Function0;", "onNavigateToSensor", "onNavigateToHistory", "onNavigateToChat", "onNavigateToFarmMap", "onNavigateToWeather", "onOpenLanguagePicker", "WeatherCard", "weather", "Lcom/cropdoc/app/data/model/WeatherData;", "isOptedIn", "", "onSetup", "WeatherStat", "emoji", "", "value", "label", "FarmMapPreview", "zones", "", "Lcom/cropdoc/app/data/model/FarmZone;", "ChatButton", "onClick", "ModelStatusCard", "state", "Lcom/cropdoc/app/data/model/ModelState;", "onRetry", "ActionCard", "icon", "Landroidx/compose/ui/graphics/vector/ImageVector;", "title", "subtitle", "color", "Landroidx/compose/ui/graphics/Color;", "enabled", "modifier", "Landroidx/compose/ui/Modifier;", "ActionCard-ww6aTOc", "(Landroidx/compose/ui/graphics/vector/ImageVector;Ljava/lang/String;Ljava/lang/String;JZLandroidx/compose/ui/Modifier;Lkotlin/jvm/functions/Function0;)V", "TipsCard", "app_debug"})
public final class HomeScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void HomeScreen(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.viewmodel.CropDocViewModel viewModel, @org.jetbrains.annotations.NotNull()
    com.cropdoc.app.viewmodel.WeatherViewModel weatherViewModel, @org.jetbrains.annotations.NotNull()
    com.cropdoc.app.viewmodel.FarmViewModel farmViewModel, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToCamera, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToSensor, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToHistory, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToChat, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToFarmMap, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToWeather, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onOpenLanguagePicker) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void WeatherCard(com.cropdoc.app.data.model.WeatherData weather, boolean isOptedIn, kotlin.jvm.functions.Function0<kotlin.Unit> onSetup) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void WeatherStat(java.lang.String emoji, java.lang.String value, java.lang.String label) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void FarmMapPreview(java.util.List<com.cropdoc.app.data.model.FarmZone> zones, kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateToFarmMap) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ChatButton(kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ModelStatusCard(com.cropdoc.app.data.model.ModelState state, kotlin.jvm.functions.Function0<kotlin.Unit> onRetry) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void TipsCard() {
    }
}