package com.cropdoc.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cropdoc.app.data.model.BleState
import com.cropdoc.app.data.model.SeverityLevel
import com.cropdoc.app.data.model.SoilReading

// ── Health Score Gauge ────────────────────────────────────────────────────────

@Composable
fun HealthScoreGauge(score: Int, modifier: Modifier = Modifier) {
    val animatedScore by animateFloatAsState(
        targetValue = score.toFloat(),
        animationSpec = tween(1000),
        label = "health_score"
    )
    val color by animateColorAsState(
        targetValue = when {
            score >= 75 -> Color(0xFF43A047)
            score >= 50 -> Color(0xFFFFA000)
            score >= 25 -> Color(0xFFF57C00)
            else        -> Color(0xFFD32F2F)
        },
        animationSpec = tween(1000),
        label = "health_color"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { animatedScore / 100f },
                modifier = Modifier.size(96.dp),
                color = color,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeWidth = 8.dp,
                strokeCap = StrokeCap.Round,
            )
            Text(
                text = "${score}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Health Score",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ── Severity Chip ─────────────────────────────────────────────────────────────

@Composable
fun SeverityChip(severity: SeverityLevel) {
    val (label, containerColor, contentColor) = when (severity) {
        SeverityLevel.LOW      -> Triple("Low",      Color(0xFFE8F5E9), Color(0xFF2E7D32))
        SeverityLevel.MEDIUM   -> Triple("Medium",   Color(0xFFFFF8E1), Color(0xFFF57C00))
        SeverityLevel.HIGH     -> Triple("High",     Color(0xFFFFF3E0), Color(0xFFE65100))
        SeverityLevel.CRITICAL -> Triple("Critical", Color(0xFFFFEBEE), Color(0xFFB71C1C))
    }
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = containerColor
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = contentColor
        )
    }
}

// ── Soil Metric Card ──────────────────────────────────────────────────────────

@Composable
fun SoilMetricCard(
    label: String,
    value: String,
    unit: String,
    icon: ImageVector,
    statusColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(statusColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = statusColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = unit,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ── BLE Status Banner ─────────────────────────────────────────────────────────

@Composable
fun BleBanner(bleState: BleState, modifier: Modifier = Modifier) {
    val (icon, text, color) = when (bleState) {
        is BleState.Connected    -> Triple(Icons.Default.Bluetooth, "Sensor: ${bleState.deviceName}", Color(0xFF43A047))
        is BleState.Connecting   -> Triple(Icons.Default.BluetoothSearching, "Connecting…", Color(0xFFFFA000))
        is BleState.Scanning     -> Triple(Icons.Default.BluetoothSearching, "Scanning for sensors…", Color(0xFF1976D2))
        is BleState.Error        -> Triple(Icons.Default.BluetoothDisabled, bleState.message, Color(0xFFD32F2F))
        BleState.Disconnected    -> Triple(Icons.Default.BluetoothDisabled, "No sensor connected", Color(0xFF757575))
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.12f),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            Text(text = text, style = MaterialTheme.typography.labelMedium, color = color)
        }
    }
}

// ── Soil Reading Panel ────────────────────────────────────────────────────────

@Composable
fun SoilReadingPanel(reading: SoilReading, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Soil Readings",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val soilGreen = Color(0xFF43A047)
                val warnAmber = Color(0xFFF57C00)

                SoilMetricCard(
                    "Moisture", "%.0f".format(reading.moisture), "%",
                    Icons.Default.WaterDrop,
                    if (reading.moisture in 30f..70f) soilGreen else warnAmber,
                    Modifier.weight(1f)
                )
                SoilMetricCard(
                    "pH", "%.1f".format(reading.ph), "pH",
                    Icons.Default.Science,
                    if (reading.ph in 5.5f..7.5f) soilGreen else warnAmber,
                    Modifier.weight(1f)
                )
                SoilMetricCard(
                    "Temp", "%.1f".format(reading.temperature), "°C",
                    Icons.Default.Thermostat,
                    if (reading.temperature in 15f..35f) soilGreen else warnAmber,
                    Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val soilGreen = Color(0xFF43A047)
                val warnAmber = Color(0xFFF57C00)

                SoilMetricCard(
                    "Nitrogen", "%.0f".format(reading.nitrogen), "mg/kg",
                    Icons.Default.Grass,
                    if (reading.nitrogen >= 40f) soilGreen else warnAmber,
                    Modifier.weight(1f)
                )
                SoilMetricCard(
                    "Phosphorus", "%.0f".format(reading.phosphorus), "mg/kg",
                    Icons.Default.EnergySavingsLeaf,
                    if (reading.phosphorus >= 20f) soilGreen else warnAmber,
                    Modifier.weight(1f)
                )
                SoilMetricCard(
                    "Potassium", "%.0f".format(reading.potassium), "mg/kg",
                    Icons.Default.Spa,
                    if (reading.potassium >= 100f) soilGreen else warnAmber,
                    Modifier.weight(1f)
                )
            }
        }
    }
}

// ── Section Header ────────────────────────────────────────────────────────────

@Composable
fun SectionHeader(title: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
