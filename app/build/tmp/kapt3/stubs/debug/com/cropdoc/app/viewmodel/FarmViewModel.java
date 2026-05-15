package com.cropdoc.app.viewmodel;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000r\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\n\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J6\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020#2\u0006\u0010%\u001a\u00020#2\u0006\u0010&\u001a\u00020#2\u0006\u0010\'\u001a\u00020(J\u000e\u0010)\u001a\u00020\u001f2\u0006\u0010*\u001a\u00020\u000bJ\u000e\u0010+\u001a\u00020\u001f2\u0006\u0010*\u001a\u00020\u000bJ\u000e\u0010,\u001a\u00020\u001f2\u0006\u0010*\u001a\u00020\u000bJ\u0006\u0010-\u001a\u00020\u001fJ\u000e\u0010.\u001a\u00020\u001f2\u0006\u0010/\u001a\u00020(J0\u00100\u001a\u00020\u001f2\u0006\u0010/\u001a\u00020(2\u0006\u0010 \u001a\u00020!2\u0006\u00101\u001a\u00020(2\u0006\u00102\u001a\u0002032\b\b\u0002\u00104\u001a\u00020!J\u000e\u00105\u001a\u00020\u001f2\u0006\u00106\u001a\u00020\u0017J\u000e\u00107\u001a\u00020\u001f2\u0006\u00106\u001a\u00020\u0017J\u000e\u00108\u001a\u00020\u001f2\u0006\u00109\u001a\u00020:J\u000e\u0010;\u001a\u0002032\u0006\u00101\u001a\u00020(J\u0016\u0010<\u001a\u0002032\u0006\u00101\u001a\u00020(2\u0006\u00102\u001a\u000203R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000b0\n0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0019\u0010\u000e\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000b0\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\rR\u0016\u0010\u0010\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000b0\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u0012\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000b0\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u001a\u0010\u0016\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00170\n0\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0018\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00170\n0\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0015R\u0016\u0010\u001a\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u001b0\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u001c\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u001b0\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0015\u00a8\u0006="}, d2 = {"Lcom/cropdoc/app/viewmodel/FarmViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "<init>", "(Landroid/app/Application;)V", "repository", "Lcom/cropdoc/app/data/repository/FarmRepository;", "allZones", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/cropdoc/app/data/model/FarmZone;", "getAllZones", "()Lkotlinx/coroutines/flow/Flow;", "activeZone", "getActiveZone", "_selectedZone", "Lkotlinx/coroutines/flow/MutableStateFlow;", "selectedZone", "Lkotlinx/coroutines/flow/StateFlow;", "getSelectedZone", "()Lkotlinx/coroutines/flow/StateFlow;", "_selectedZoneCrops", "Lcom/cropdoc/app/data/model/Crop;", "selectedZoneCrops", "getSelectedZoneCrops", "_selectedZoneLatestReading", "Lcom/cropdoc/app/data/model/SoilReadingHistory;", "selectedZoneLatestReading", "getSelectedZoneLatestReading", "addZone", "", "name", "", "x", "", "y", "width", "height", "color", "", "updateZone", "zone", "deleteZone", "selectZone", "clearSelectedZone", "setActiveZone", "zoneId", "addCrop", "plantedDate", "expectedHarvestDays", "", "notes", "updateCrop", "crop", "deleteCrop", "saveReadingToActiveZone", "soilReading", "Lcom/cropdoc/app/data/model/SoilReading;", "getDaysPlanted", "getDaysToHarvest", "app_debug"})
public final class FarmViewModel extends androidx.lifecycle.AndroidViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.cropdoc.app.data.repository.FarmRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.cropdoc.app.data.model.FarmZone>> allZones = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<com.cropdoc.app.data.model.FarmZone> activeZone = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.cropdoc.app.data.model.FarmZone> _selectedZone = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.cropdoc.app.data.model.FarmZone> selectedZone = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<com.cropdoc.app.data.model.Crop>> _selectedZoneCrops = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.cropdoc.app.data.model.Crop>> selectedZoneCrops = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.cropdoc.app.data.model.SoilReadingHistory> _selectedZoneLatestReading = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.cropdoc.app.data.model.SoilReadingHistory> selectedZoneLatestReading = null;
    
    public FarmViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application application) {
        super(null);
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.cropdoc.app.data.model.FarmZone>> getAllZones() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.cropdoc.app.data.model.FarmZone> getActiveZone() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.cropdoc.app.data.model.FarmZone> getSelectedZone() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.cropdoc.app.data.model.Crop>> getSelectedZoneCrops() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.cropdoc.app.data.model.SoilReadingHistory> getSelectedZoneLatestReading() {
        return null;
    }
    
    public final void addZone(@org.jetbrains.annotations.NotNull()
    java.lang.String name, float x, float y, float width, float height, long color) {
    }
    
    public final void updateZone(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.data.model.FarmZone zone) {
    }
    
    public final void deleteZone(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.data.model.FarmZone zone) {
    }
    
    public final void selectZone(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.data.model.FarmZone zone) {
    }
    
    public final void clearSelectedZone() {
    }
    
    public final void setActiveZone(long zoneId) {
    }
    
    public final void addCrop(long zoneId, @org.jetbrains.annotations.NotNull()
    java.lang.String name, long plantedDate, int expectedHarvestDays, @org.jetbrains.annotations.NotNull()
    java.lang.String notes) {
    }
    
    public final void updateCrop(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.data.model.Crop crop) {
    }
    
    public final void deleteCrop(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.data.model.Crop crop) {
    }
    
    public final void saveReadingToActiveZone(@org.jetbrains.annotations.NotNull()
    com.cropdoc.app.data.model.SoilReading soilReading) {
    }
    
    public final int getDaysPlanted(long plantedDate) {
        return 0;
    }
    
    public final int getDaysToHarvest(long plantedDate, int expectedHarvestDays) {
        return 0;
    }
}