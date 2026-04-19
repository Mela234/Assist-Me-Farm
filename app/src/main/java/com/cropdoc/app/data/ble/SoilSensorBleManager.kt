package com.cropdoc.app.data.ble

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.cropdoc.app.data.model.BleDevice
import com.cropdoc.app.data.model.BleState
import com.cropdoc.app.data.model.SoilReading
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * Manages BLE communication with the soil sensor (ESP32/Arduino).
 *
 * Expected BLE service layout:
 *   Service UUID  : SOIL_SERVICE_UUID
 *   Characteristic: SOIL_DATA_UUID  (notify)
 *     Payload     : 24 bytes
 *       [0..3]   moisture   float32 LE
 *       [4..7]   ph         float32 LE
 *       [8..11]  nitrogen   float32 LE
 *       [12..15] phosphorus float32 LE
 *       [16..19] potassium  float32 LE
 *       [20..23] temperature float32 LE
 */
class SoilSensorBleManager(private val context: Context) {

    companion object {
        private const val TAG = "SoilBLE"
        val SOIL_SERVICE_UUID: UUID = UUID.fromString("12345678-1234-5678-1234-56789abcdef0")
        val SOIL_DATA_UUID: UUID    = UUID.fromString("12345678-1234-5678-1234-56789abcdef1")
        val CLIENT_CHAR_CONFIG: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
        private const val SCAN_PERIOD_MS = 15_000L
    }

    private val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? get() = bluetoothManager.adapter
    private var bluetoothGatt: BluetoothGatt? = null

    private val _bleState = MutableStateFlow<BleState>(BleState.Disconnected)
    val bleState: StateFlow<BleState> = _bleState.asStateFlow()

    private val _soilReading = MutableStateFlow<SoilReading?>(null)
    val soilReading: StateFlow<SoilReading?> = _soilReading.asStateFlow()

    private val _scannedDevices = MutableStateFlow<List<BleDevice>>(emptyList())
    val scannedDevices: StateFlow<List<BleDevice>> = _scannedDevices.asStateFlow()

    // ── Permissions ──────────────────────────────────────────────────────────

    fun hasRequiredPermissions(): Boolean {
        val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            listOf(Manifest.permission.BLUETOOTH, Manifest.permission.ACCESS_FINE_LOCATION)
        }
        return perms.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    // ── Scanning ─────────────────────────────────────────────────────────────

    fun startScan() {
        if (!hasRequiredPermissions()) {
            _bleState.value = BleState.Error("Bluetooth permissions not granted")
            return
        }
        val adapter = bluetoothAdapter ?: run {
            _bleState.value = BleState.Error("Bluetooth not available")
            return
        }
        if (!adapter.isEnabled) {
            _bleState.value = BleState.Error("Bluetooth is disabled")
            return
        }
        _scannedDevices.value = emptyList()
        _bleState.value = BleState.Scanning

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        // Filter to only show devices advertising our soil service
        val filters = listOf(
            ScanFilter.Builder()
                .setServiceUuid(android.os.ParcelUuid(SOIL_SERVICE_UUID))
                .build()
        )

        adapter.bluetoothLeScanner?.startScan(filters, settings, scanCallback)

        // Auto-stop after timeout
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            stopScan()
        }, SCAN_PERIOD_MS)
    }

    fun stopScan() {
        if (!hasRequiredPermissions()) return
        bluetoothAdapter?.bluetoothLeScanner?.stopScan(scanCallback)
        if (_bleState.value is BleState.Scanning) {
            _bleState.value = BleState.Disconnected
        }
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            if (!hasRequiredPermissions()) return
            val device = result.device
            val name = device.name ?: "Unknown Sensor"
            val bleDevice = BleDevice(name, device.address, result.rssi)
            val current = _scannedDevices.value.toMutableList()
            if (current.none { it.address == bleDevice.address }) {
                _scannedDevices.value = current + bleDevice
            }
        }

        override fun onScanFailed(errorCode: Int) {
            _bleState.value = BleState.Error("Scan failed with code $errorCode")
        }
    }

    // ── Connection ───────────────────────────────────────────────────────────

    fun connect(address: String) {
        if (!hasRequiredPermissions()) return
        val device = bluetoothAdapter?.getRemoteDevice(address) ?: return
        _bleState.value = BleState.Connecting(device.name ?: address)
        bluetoothGatt = device.connectGatt(context, false, gattCallback, BluetoothDevice_TRANSPORT_LE)
    }

    fun disconnect() {
        if (!hasRequiredPermissions()) return
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
        _bleState.value = BleState.Disconnected
    }

    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (!hasRequiredPermissions()) return
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.d(TAG, "Connected — discovering services")
                    gatt.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.d(TAG, "Disconnected")
                    _bleState.value = BleState.Disconnected
                    bluetoothGatt?.close()
                    bluetoothGatt = null
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                _bleState.value = BleState.Error("Service discovery failed")
                return
            }
            val characteristic = gatt
                .getService(SOIL_SERVICE_UUID)
                ?.getCharacteristic(SOIL_DATA_UUID)
            if (characteristic == null) {
                _bleState.value = BleState.Error("Soil service not found on device")
                return
            }
            // Enable notifications
            if (!hasRequiredPermissions()) return
            gatt.setCharacteristicNotification(characteristic, true)
            val descriptor = characteristic.getDescriptor(CLIENT_CHAR_CONFIG)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                gatt.writeDescriptor(descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
            } else {
                @Suppress("DEPRECATION")
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                @Suppress("DEPRECATION")
                gatt.writeDescriptor(descriptor)
            }
            val deviceName = if (hasRequiredPermissions()) gatt.device.name ?: gatt.device.address
                             else gatt.device.address
            _bleState.value = BleState.Connected(deviceName, gatt.device.address)
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            if (characteristic.uuid == SOIL_DATA_UUID) {
                parseSoilData(value)?.let { _soilReading.value = it }
            }
        }

        // Android < 13 fallback
        @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            if (characteristic.uuid == SOIL_DATA_UUID) {
                parseSoilData(characteristic.value)?.let { _soilReading.value = it }
            }
        }
    }

    // ── Parsing ──────────────────────────────────────────────────────────────

    private fun parseSoilData(bytes: ByteArray): SoilReading? {
        if (bytes.size < 24) return null
        return try {
            val buf = java.nio.ByteBuffer.wrap(bytes).order(java.nio.ByteOrder.LITTLE_ENDIAN)
            SoilReading(
                moisture    = buf.float,
                ph          = buf.float,
                nitrogen    = buf.float,
                phosphorus  = buf.float,
                potassium   = buf.float,
                temperature = buf.float
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse soil data", e)
            null
        }
    }

    // Transport constant available at runtime even on older APIs
    private val BluetoothDevice_TRANSPORT_LE = 2
}
