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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cropdoc.app.R
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
    val bleState           by viewModel.bleState.collectAsState()
    val soilReading        by viewModel.soilReading.collectAsState()
    val scannedDevices     by viewModel.scannedDevices.collectAsState()
    val mockActive         by viewModel.mockSensorActive.collectAsState()
    val soilSummary        by viewModel.soilSummary.collectAsState()
    val soilSummaryLoading by viewModel.soilSummaryLoading.collectAsState()

    val isBleConnected = bleState is BleState.Connected

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.sensor_title),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            stringResource(R.string.cd_back),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
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

            // BLE banner
            item { BleBanner(bleState) }

            // Live soil reading + summary
            item {
                AnimatedVisibility(
                    visible = soilReading != null,
                    enter   = fadeIn() + expandVertically(),
                    exit    = fadeOut() + shrinkVertically()
                ) {
                    soilReading?.let {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            SoilReadingPanel(it)

                            OutlinedButton(
                                onClick  = { viewModel.summariseSoil() },
                                modifier = Modifier.fillMaxWidth(),
                                shape    = RoundedCornerShape(14.dp),
                                enabled  = !soilSummaryLoading
                            ) {
                                if (soilSummaryLoading) {
                                    CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                                    Spacer(Modifier.width(8.dp))
                                    Text(stringResource(R.string.sensor_getting_summary))
                                } else {
                                    Icon(Icons.Default.Summarize, null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text(stringResource(R.string.sensor_explain_readings))
                                }
                            }

                            AnimatedVisibility(visible = soilSummary != null) {
                                soilSummary?.let { summary ->
                                    Card(
                                        shape  = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(Icons.Default.Agriculture, null,
                                                    tint = Color(0xFF2E7D32),
                                                    modifier = Modifier.size(18.dp))
                                                Text(
                                                    stringResource(R.string.sensor_summary_title),
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color(0xFF1B5E20)
                                                )
                                            }
                                            Spacer(Modifier.height(8.dp))
                                            Text(summary,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color(0xFF2E7D32))
                                            Spacer(Modifier.height(8.dp))
                                            TextButton(
                                                onClick = { viewModel.clearSoilSummary() },
                                                contentPadding = PaddingValues(0.dp)
                                            ) {
                                                Text(
                                                    stringResource(R.string.sensor_dismiss),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Color(0xFF388E3C)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Connection controls
            item {
                if (!mockActive) {
                    when (bleState) {
                        BleState.Disconnected -> {
                            Button(
                                onClick  = { viewModel.startBleScan() },
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                shape    = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Default.BluetoothSearching, null)
                                Spacer(Modifier.width(8.dp))
                                Text(stringResource(R.string.sensor_scan))
                            }
                        }
                        BleState.Scanning -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                LinearProgressIndicator(
                                    Modifier.weight(1f).align(Alignment.CenterVertically)
                                )
                                OutlinedButton(onClick = { viewModel.stopBleScan() }) {
                                    Text(stringResource(R.string.sensor_stop))
                                }
                            }
                        }
                        is BleState.Connected -> {
                            OutlinedButton(
                                onClick  = { viewModel.disconnectSensor() },
                                modifier = Modifier.fillMaxWidth(),
                                colors   = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFFD32F2F)
                                )
                            ) {
                                Icon(Icons.Default.BluetoothDisabled, null)
                                Spacer(Modifier.width(8.dp))
                                Text(stringResource(R.string.sensor_disconnect))
                            }
                        }
                        is BleState.Connecting -> {
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                                Text(stringResource(R.string.sensor_connecting,
                                    (bleState as BleState.Connecting).deviceName))
                            }
                        }
                        is BleState.Error -> {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    (bleState as BleState.Error).message,
                                    color    = Color(0xFFD32F2F),
                                    modifier = Modifier.weight(1f)
                                )
                                TextButton(onClick = { viewModel.startBleScan() }) {
                                    Text(stringResource(R.string.model_retry))
                                }
                            }
                        }
                    }
                }
            }

            // Discovered devices list
            if (scannedDevices.isNotEmpty()) {
                item {
                    Text(
                        stringResource(R.string.sensor_nearby),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                items(scannedDevices) { device ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                            .clickable { viewModel.connectToSensor(device.address) },
                        shape  = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(Icons.Default.Sensors, null,
                                tint = MaterialTheme.colorScheme.primary)
                            Column(Modifier.weight(1f)) {
                                Text(device.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Medium)
                                Text(device.address,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Icon(Icons.Default.SignalCellularAlt, null,
                                    tint = signalColor(device.rssi),
                                    modifier = Modifier.size(16.dp))
                                Text("${device.rssi} dBm",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            // Mock sensor card — hidden when BLE is connected
            item {
                if (!isBleConnected) {
                    MockSensorCard(
                        isActive  = mockActive,
                        soilReading = soilReading,
                        onEnable  = { viewModel.enableMockSensor() },
                        onDisable = { viewModel.disableMockSensor() }
                    )
                }
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
    isActive    : Boolean,
    soilReading : com.cropdoc.app.data.model.SoilReading?,
    onEnable    : () -> Unit,
    onDisable   : () -> Unit
) {
    Card(
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) Color(0xFFE8F5E9)
            else MaterialTheme.colorScheme.surfaceVariant
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
                    Icon(Icons.Default.DeveloperMode, null,
                        tint = if (isActive) Color(0xFF2E7D32)
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp))
                    Column {
                        Text(stringResource(R.string.sensor_mock_title),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isActive) Color(0xFF1B5E20)
                            else MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(stringResource(R.string.sensor_mock_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isActive) Color(0xFF388E3C)
                            else MaterialTheme.colorScheme.onSurfaceVariant)
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

            AnimatedVisibility(visible = isActive && soilReading != null) {
                soilReading?.let { r ->
                    Column(modifier = Modifier.padding(top = 12.dp)) {
                        HorizontalDivider(color = Color(0xFF43A047).copy(alpha = 0.3f))
                        Spacer(Modifier.height(10.dp))
                        Text(stringResource(R.string.sensor_mock_simulating),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(6.dp))
                        val readings = listOf(
                            stringResource(R.string.soil_moisture) to "%.0f%%".format(r.moisture),
                            stringResource(R.string.soil_ph)       to "%.1f".format(r.ph),
                            "N"                                     to "%.0f mg/kg".format(r.nitrogen),
                            "P"                                     to "%.0f mg/kg".format(r.phosphorus),
                            "K"                                     to "%.0f mg/kg".format(r.potassium),
                            stringResource(R.string.soil_temp)     to "%.1f°C".format(r.temperature)
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
                        Text(stringResource(R.string.sensor_mock_warning),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF555555))
                    }
                }
            }
        }
    }
}
