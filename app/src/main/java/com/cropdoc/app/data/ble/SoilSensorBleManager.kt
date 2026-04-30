package com.cropdoc.app.data.ble

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
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

class SoilSensorBleManager(private val context: Context) {

    companion object {
        private const val TAG = "SoilBLE"
        private const val TRANSPORT_LE = 2
        private const val SCAN_PERIOD_MS = 15_000L

        val SOIL_SERVICE_UUID: UUID  = UUID.fromString("12345678-1234-5678-1234-56789abcdef0")
        val SOIL_DATA_UUID: UUID     = UUID.fromString("12345678-1234-5678-1234-56789abcdef1")
        val CLIENT_CHAR_CONFIG: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

        // Known ESP32 / CropDoc device name patterns
        private val KNOWN_SENSOR_NAMES = listOf(
            "cropdoc", "esp32", "soil", "sensor", "arduino"
        )
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

    // ── Permissions ───────────────────────────────────────────────────────────

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

    // ── Scanning ──────────────────────────────────────────────────────────────
    @SuppressLint("MissingPermission")
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

        // Scan ALL nearby BLE devices — no service UUID filter
        // This lets us see the ESP32 even before the firmware is fully configured
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        adapter.bluetoothLeScanner?.startScan(null, settings, scanCallback)

        // Auto-stop after timeout
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            stopScan()
        }, SCAN_PERIOD_MS)
    }
    @SuppressLint("MissingPermission")
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
            val name = try {
                device.name ?: "Unknown Device"
            } catch (e: SecurityException) {
                "Unknown Device"
            }

            // Flag if this looks like a CropDoc/ESP32 sensor
            val isSensor = looksLikeSensor(name, result)

            val bleDevice = BleDevice(
                name = if (isSensor) "🌱 $name" else name,
                address = device.address,
                rssi = result.rssi,
                isCropDocSensor = isSensor
            )

            val current = _scannedDevices.value.toMutableList()
            val existingIndex = current.indexOfFirst { it.address == bleDevice.address }
            if (existingIndex >= 0) {
                // Update RSSI if already in list
                current[existingIndex] = bleDevice
            } else {
                // Sort — CropDoc sensors first, then by signal strength
                current.add(bleDevice)
                current.sortWith(compareByDescending<BleDevice> { it.isCropDocSensor }
                    .thenByDescending { it.rssi })
            }
            _scannedDevices.value = current
        }

        override fun onScanFailed(errorCode: Int) {
            val reason = when (errorCode) {
                ScanCallback.SCAN_FAILED_ALREADY_STARTED -> "Scan already started"
                ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED -> "App registration failed"
                ScanCallback.SCAN_FAILED_FEATURE_UNSUPPORTED -> "BLE not supported"
                ScanCallback.SCAN_FAILED_INTERNAL_ERROR -> "Internal error"
                else -> "Scan failed (code $errorCode)"
            }
            _bleState.value = BleState.Error(reason)
        }
    }

    /**
     * Heuristic to detect if a device is likely our ESP32 soil sensor.
     * Checks device name and whether it advertises our service UUID.
     */
    private fun looksLikeSensor(name: String, result: ScanResult): Boolean {
        val nameLower = name.lowercase()
        if (KNOWN_SENSOR_NAMES.any { nameLower.contains(it) }) return true

        // Check if it advertises our service UUID
        val serviceUuids = result.scanRecord?.serviceUuids
        if (serviceUuids != null) {
            return serviceUuids.any { it.uuid == SOIL_SERVICE_UUID }
        }
        return false
    }

    // ── Connection ────────────────────────────────────────────────────────────
    @SuppressLint("MissingPermission")
    fun connect(address: String) {
        if (!hasRequiredPermissions()) return
        val device = bluetoothAdapter?.getRemoteDevice(address) ?: return

        val deviceName = try { device.name ?: address } catch (e: SecurityException) { address }
        _bleState.value = BleState.Connecting(deviceName)

        bluetoothGatt = device.connectGatt(context, false, gattCallback, TRANSPORT_LE)
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        if (!hasRequiredPermissions()) return
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
        _bleState.value = BleState.Disconnected
        _soilReading.value = null
    }

    // ── GATT Callback ─────────────────────────────────────────────────────────
    @SuppressLint("MissingPermission")
    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (!hasRequiredPermissions()) return
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Log.d(TAG, "Connected — discovering services")
                    gatt.discoverServices()
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Log.d(TAG, "Disconnected (status=$status)")
                    _bleState.value = BleState.Disconnected
                    _soilReading.value = null
                    bluetoothGatt?.close()
                    bluetoothGatt = null
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) {
                _bleState.value = BleState.Error("Service discovery failed (status=$status)")
                return
            }

            // Log all services so we can debug ESP32 firmware UUID mismatches
            Log.d(TAG, "Services discovered:")
            gatt.services.forEach { service ->
                Log.d(TAG, "  Service: ${service.uuid}")
                service.characteristics.forEach { char ->
                    Log.d(TAG, "    Characteristic: ${char.uuid}")
                }
            }

            val characteristic = gatt
                .getService(SOIL_SERVICE_UUID)
                ?.getCharacteristic(SOIL_DATA_UUID)

            if (characteristic == null) {
                // Connected to a device but it's not our sensor
                // Still mark as connected so user knows BLE works
                val deviceName = try {
                    gatt.device.name ?: gatt.device.address
                } catch (e: SecurityException) {
                    gatt.device.address
                }
                _bleState.value = BleState.Error(
                    "Connected to $deviceName but soil service not found. " +
                            "Check ESP32 firmware UUID."
                )
                return
            }

            // Enable notifications
            if (!hasRequiredPermissions()) return
            gatt.setCharacteristicNotification(characteristic, true)

            val descriptor = characteristic.getDescriptor(CLIENT_CHAR_CONFIG)
            if (descriptor != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    gatt.writeDescriptor(
                        descriptor,
                        BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    )
                } else {
                    @Suppress("DEPRECATION")
                    descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    @Suppress("DEPRECATION")
                    gatt.writeDescriptor(descriptor)
                }
            }

            val deviceName = try {
                gatt.device.name ?: gatt.device.address
            } catch (e: SecurityException) {
                gatt.device.address
            }
            _bleState.value = BleState.Connected(deviceName, gatt.device.address)
            Log.d(TAG, "Soil sensor connected and notifications enabled")
        }

        // Android 13+
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            if (characteristic.uuid == SOIL_DATA_UUID) {
                parseSoilData(value)?.let {
                    Log.d(TAG, "Soil reading: $it")
                    _soilReading.value = it
                }
            }
        }

        // Android < 13
        @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            if (characteristic.uuid == SOIL_DATA_UUID) {
                parseSoilData(characteristic.value)?.let {
                    Log.d(TAG, "Soil reading: $it")
                    _soilReading.value = it
                }
            }
        }
    }

    // ── Parsing ───────────────────────────────────────────────────────────────

    private fun parseSoilData(bytes: ByteArray): SoilReading? {
        if (bytes.size < 24) {
            Log.w(TAG, "Payload too short: ${bytes.size} bytes (expected 24)")
            return null
        }
        return try {
            val buf = java.nio.ByteBuffer.wrap(bytes)
                .order(java.nio.ByteOrder.LITTLE_ENDIAN)
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
}