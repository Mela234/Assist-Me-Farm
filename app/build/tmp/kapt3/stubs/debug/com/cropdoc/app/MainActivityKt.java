package com.cropdoc.app;

@kotlin.Metadata(mv = {2, 3, 0}, k = 2, xi = 48, d1 = {"\u0000X\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u0016\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00032\u0006\u0010\u0015\u001a\u00020\n\u001a(\u0010\u0016\u001a\u00020\u00132\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001eH\u0007\u001a2\u0010\u001f\u001a\u00020\u00132\u0006\u0010 \u001a\u00020\n2\f\u0010!\u001a\b\u0012\u0004\u0012\u00020\u00130\"2\u0012\u0010#\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00130$H\u0007\"%\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001*\u00020\u00038FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0006\u0010\u0007\u001a\u0004\b\u0004\u0010\u0005\"\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011\u00a8\u0006%"}, d2 = {"dataStore", "Landroidx/datastore/core/DataStore;", "Landroidx/datastore/preferences/core/Preferences;", "Landroid/content/Context;", "getDataStore", "(Landroid/content/Context;)Landroidx/datastore/core/DataStore;", "dataStore$delegate", "Lkotlin/properties/ReadOnlyProperty;", "LANGUAGE_KEY", "Landroidx/datastore/preferences/core/Preferences$Key;", "", "getLANGUAGE_KEY", "()Landroidx/datastore/preferences/core/Preferences$Key;", "SUPPORTED_LANGUAGES", "", "Lcom/cropdoc/app/AppLanguage;", "getSUPPORTED_LANGUAGES", "()Ljava/util/List;", "applyLocale", "", "context", "languageCode", "CropDocApp", "cropDocViewModel", "Lcom/cropdoc/app/viewmodel/CropDocViewModel;", "farmViewModel", "Lcom/cropdoc/app/viewmodel/FarmViewModel;", "chatViewModel", "Lcom/cropdoc/app/viewmodel/ChatViewModel;", "weatherViewModel", "Lcom/cropdoc/app/viewmodel/WeatherViewModel;", "LanguagePickerDialog", "currentCode", "onDismiss", "Lkotlin/Function0;", "onLanguageSelected", "Lkotlin/Function1;", "app_debug"})
public final class MainActivityKt {
    @org.jetbrains.annotations.NotNull()
    private static final kotlin.properties.ReadOnlyProperty dataStore$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> LANGUAGE_KEY = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<com.cropdoc.app.AppLanguage> SUPPORTED_LANGUAGES = null;
    
    @org.jetbrains.annotations.NotNull()
    public static final androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> getDataStore(@org.jetbrains.annotations.NotNull()
    android.content.Context $this$dataStore) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getLANGUAGE_KEY() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static final java.util.List<com.cropdoc.app.AppLanguage> getSUPPORTED_LANGUAGES() {
        return null;
    }
    
    public static final void applyLocale(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String languageCode) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void CropDocApp(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.viewmodel.CropDocViewModel cropDocViewModel, @org.jetbrains.annotations.NotNull()
    com.cropdoc.app.viewmodel.FarmViewModel farmViewModel, @org.jetbrains.annotations.NotNull()
    com.cropdoc.app.viewmodel.ChatViewModel chatViewModel, @org.jetbrains.annotations.NotNull()
    com.cropdoc.app.viewmodel.WeatherViewModel weatherViewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void LanguagePickerDialog(@org.jetbrains.annotations.NotNull()
    java.lang.String currentCode, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onLanguageSelected) {
    }
}