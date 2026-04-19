package com.cropdoc.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cropdoc.app.data.model.BleState
import com.cropdoc.app.ui.components.BleBanner
import com.cropdoc.app.ui.components.SoilReadingPanel
import com.cropdoc.app.viewmodel.CropDocViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorScreen(
    viewModel: CropDocViewModel,
    onNavigateBack: () -> Unit
) {
    val bleState by viewModel.bleState.collectAsState()
    val soilReading by viewModel.soilReading.collectAsState()
    val scannedDevices by viewModel.scannedDevices.collectAsState()
    val mockActive by viewModel.mockSensorActive.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Soil Sensor", fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimary)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back",
                            tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Current BLE status
            item {
                BleBanner(bleState)
            }

            // Live soil reading
            item {
                AnimatedVisibility(
                    visible = soilReading != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    soilReading?.let { SoilReadingPanel(it) }
                }
            }

            // Connection controls
            item {
                when (bleState) {
                    BleState.Disconnected -> {
                        Button(
                            onClick = { viewModel.startBleScan() },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.BluetoothSearching, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Scan for Soil Sensor")
                        }
                    }
                    BleState.Scanning -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            LinearProgressIndicator(Modifier.weight(1f).align(Alignment.CenterVertically))
                            OutlinedButton(onClick = { viewModel.stopBleScan() }) {
                                Text("Stop")
                            }
                        }
                    }
                    is BleState.Connected -> {
                        OutlinedButton(
                            onClick = { viewModel.disconnectSensor() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFD32F2F)
                            )
                        ) {
                            Icon(Icons.Default.BluetoothDisabled, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Disconnect Sensor")
                        }
                    }
                    is BleState.Connecting -> {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                            Text("Connecting to ${(bleState as BleState.Connecting).deviceName}…")
                        }
                    }
                    is BleState.Error -> {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                (bleState as BleState.Error).message,
                                color = Color(0xFFD32F2F),
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = { viewModel.startBleScan() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }

            // Discovered devices list
            if (scannedDevices.isNotEmpty()) {
                item {
                    Text(
                        "Nearby Sensors",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                items(scannedDevices) { device ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.connectToSensor(device.address) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Sensors,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Column(Modifier.weight(1f)) {
                                Text(
                                    device.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    device.address,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Icon(Icons.Default.SignalCellularAlt, null,
                                    tint = signalColor(device.rssi),
                                    modifier = Modifier.size(16.dp))
                                Text(
                                    "${device.rssi} dBm",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Mock sensor card — for development without hardware
            item {
                MockSensorCard(
                    isActive = mockActive,
                    soilReading = soilReading,
                    onEnable  = { viewModel.enableMockSensor() },
                    onDisable = { viewModel.disableMockSensor() }
                )
            }

            // Setup guide
            item {
                SensorSetupGuide()
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

private fun signalColor(rssi: Int) = when {
    rssi >= -60 -> Color(0xFF43A047)
    rssi >= -80 -> Color(0xFFFFA000)
    else        -> Color(0xFFD32F2F)
}

@Composable
private fun MockSensorCard(
    isActive: Boolean,
    soilReading: com.cropdoc.app.data.model.SoilReading?,
    onEnable: () -> Unit,
    onDisable: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive)
                Color(0xFFE8F5E9)
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (isActive)
            androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF43A047))
        else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.DeveloperMode,
                        contentDescription = null,
                        tint = if (isActive) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            "Mock Soil Sensor",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isActive) Color(0xFF1B5E20)
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Development mode — no hardware needed",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isActive) Color(0xFF388E3C)
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Switch(
                    checked = isActive,
                    onCheckedChange = { if (it) onEnable() else onDisable() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF43A047)
                    )
                )
            }

            // Show live mock readings while active
            AnimatedVisibility(visible = isActive && soilReading != null) {
                soilReading?.let { r ->
                    Column(modifier = Modifier.padding(top = 12.dp)) {
                        HorizontalDivider(color = Color(0xFF43A047).copy(alpha = 0.3f))
                        Spacer(Modifier.height(10.dp))
                        Text(
                            "📡 Simulating live readings",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(6.dp))
                        // Mini reading grid
                        val readings = listOf(
                            "Moisture" to "%.0f%%".format(r.moisture),
                            "pH" to "%.1f".format(r.ph),
                            "N" to "%.0f mg/kg".format(r.nitrogen),
                            "P" to "%.0f mg/kg".format(r.phosphorus),
                            "K" to "%.0f mg/kg".format(r.potassium),
                            "Temp" to "%.1f°C".format(r.temperature)
                        )
                        readings.chunked(3).forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                row.forEach { (label, value) ->
                                    Column(Modifier.weight(1f)) {
                                        Text(label,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFF388E3C))
                                        Text(value,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF1B5E20))
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "⚠ Readings simulate a low-moisture, acidic soil profile " +
                            "so the AI has something meaningful to analyse.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF555555)
                        )
                    }
                }
            }
        }
    }
}
@Composable
private fun SensorSetupGuide() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Info, null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp))
                Text(
                    "Sensor Setup",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(10.dp))

            val steps = listOf(
                "Build sensor with ESP32 + NPK/pH/moisture probes",
                "Flash firmware with Service UUID: 12345678-1234-5678-1234-56789abcdef0",
                "Data characteristic UUID: ...abcdef1 (24-byte float payload)",
                "Power on sensor and press Scan above",
                "Tap your sensor name to connect"
            )
            steps.forEachIndexed { i, step ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = androidx.compose.foundation.shape.CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                "${i + 1}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        step,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
