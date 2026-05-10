package com.cropdoc.app.data.ble;

@kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000\u0080\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0012\n\u0002\b\u0002\u0018\u0000 42\u00020\u0001:\u00014B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u001e\u001a\u00020\u001fJ\b\u0010 \u001a\u00020!H\u0007J\b\u0010\"\u001a\u00020!H\u0007J\u0018\u0010&\u001a\u00020\u001f2\u0006\u0010\'\u001a\u00020(2\u0006\u0010)\u001a\u00020*H\u0002J\u0010\u0010+\u001a\u00020!2\u0006\u0010,\u001a\u00020(H\u0007J\b\u0010-\u001a\u00020!H\u0007J\u0012\u00101\u001a\u0004\u0018\u00010\u00162\u0006\u00102\u001a\u000203H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\b\u001a\u0004\u0018\u00010\t8BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\n\u0010\u000bR\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00100\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0016\u0010\u0015\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00160\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u0017\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00160\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0014R\u001a\u0010\u0019\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001b0\u001a0\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u001c\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001b0\u001a0\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0014R\u0010\u0010#\u001a\u00020$X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010%R\u0012\u0010.\u001a\u00020/8\u0002X\u0083\u0004\u00a2\u0006\u0004\n\u0002\u00100\u00a8\u00065"}, d2 = {"Lcom/cropdoc/app/data/ble/SoilSensorBleManager;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "bluetoothManager", "Landroid/bluetooth/BluetoothManager;", "bluetoothAdapter", "Landroid/bluetooth/BluetoothAdapter;", "getBluetoothAdapter", "()Landroid/bluetooth/BluetoothAdapter;", "bluetoothGatt", "Landroid/bluetooth/BluetoothGatt;", "_bleState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/cropdoc/app/data/model/BleState;", "bleState", "Lkotlinx/coroutines/flow/StateFlow;", "getBleState", "()Lkotlinx/coroutines/flow/StateFlow;", "_soilReading", "Lcom/cropdoc/app/data/model/SoilReading;", "soilReading", "getSoilReading", "_scannedDevices", "", "Lcom/cropdoc/app/data/model/BleDevice;", "scannedDevices", "getScannedDevices", "hasRequiredPermissions", "", "startScan", "", "stopScan", "scanCallback", "Landroid/bluetooth/le/ScanCallback;", "Landroid/bluetooth/le/ScanCallback;", "looksLikeSensor", "name", "", "result", "Landroid/bluetooth/le/ScanResult;", "connect", "address", "disconnect", "gattCallback", "Landroid/bluetooth/BluetoothGattCallback;", "Landroid/bluetooth/BluetoothGattCallback;", "parseSoilData", "bytes", "", "Companion", "app_debug"})
public final class SoilSensorBleManager {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "SoilBLE";
    private static final int TRANSPORT_LE = 2;
    private static final long SCAN_PERIOD_MS = 15000L;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.UUID SOIL_SERVICE_UUID = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.UUID SOIL_DATA_UUID = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.UUID CLIENT_CHAR_CONFIG = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.String> KNOWN_SENSOR_NAMES = null;
    @org.jetbrains.annotations.NotNull()
    private final android.bluetooth.BluetoothManager bluetoothManager = null;
    @org.jetbrains.annotations.Nullable()
    private android.bluetooth.BluetoothGatt bluetoothGatt;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.cropdoc.app.data.model.BleState> _bleState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.cropdoc.app.data.model.BleState> bleState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.cropdoc.app.data.model.SoilReading> _soilReading = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<com.cropdoc.app.data.model.SoilReading> soilReading = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<com.cropdoc.app.data.model.BleDevice>> _scannedDevices = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<com.cropdoc.app.data.model.BleDevice>> scannedDevices = null;
    @org.jetbrains.annotations.NotNull()
    private final android.bluetooth.le.ScanCallback scanCallback = null;
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    @org.jetbrains.annotations.NotNull()
    private final android.bluetooth.BluetoothGattCallback gattCallback = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.cropdoc.app.data.ble.SoilSensorBleManager.Companion Companion = null;
    
    public SoilSensorBleManager(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    private final android.bluetooth.BluetoothAdapter getBluetoothAdapter() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.cropdoc.app.data.model.BleState> getBleState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<com.cropdoc.app.data.model.SoilReading> getSoilReading() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<com.cropdoc.app.data.model.BleDevice>> getScannedDevices() {
        return null;
    }
    
    public final boolean hasRequiredPermissions() {
        return false;
    }
    
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    public final void startScan() {
    }
    
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    public final void stopScan() {
    }
    
    /**
     * Heuristic to detect if a device is likely our ESP32 soil sensor.
     * Checks device name and whether it advertises our service UUID.
     */
    private final boolean looksLikeSensor(java.lang.String name, android.bluetooth.le.ScanResult result) {
        return false;
    }
    
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    public final void connect(@org.jetbrains.annotations.NotNull()
    java.lang.String address) {
    }
    
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    public final void disconnect() {
    }
    
    private final com.cropdoc.app.data.model.SoilReading parseSoilData(byte[] bytes) {
        return null;
    }
    
    @kotlin.Metadata(mv = {2, 3, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010 \n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082T\u00a2\u0006\u0002\n\u0000R\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u000e\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\rR\u0011\u0010\u0010\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\rR\u0014\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00050\u0013X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lcom/cropdoc/app/data/ble/SoilSensorBleManager$Companion;", "", "<init>", "()V", "TAG", "", "TRANSPORT_LE", "", "SCAN_PERIOD_MS", "", "SOIL_SERVICE_UUID", "Ljava/util/UUID;", "getSOIL_SERVICE_UUID", "()Ljava/util/UUID;", "SOIL_DATA_UUID", "getSOIL_DATA_UUID", "CLIENT_CHAR_CONFIG", "getCLIENT_CHAR_CONFIG", "KNOWN_SENSOR_NAMES", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.UUID getSOIL_SERVICE_UUID() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.UUID getSOIL_DATA_UUID() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.UUID getCLIENT_CHAR_CONFIG() {
            return null;
        }
    }
}