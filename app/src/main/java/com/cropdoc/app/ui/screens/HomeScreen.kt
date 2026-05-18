package com.cropdoc.app.ui.screens

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cropdoc.app.CropDocApplication
import com.cropdoc.app.R
import com.cropdoc.app.data.model.FarmZone
import com.cropdoc.app.data.model.ModelState
import com.cropdoc.app.data.model.WeatherData
import com.cropdoc.app.ui.components.BleBanner
import com.cropdoc.app.ui.components.SoilReadingPanel
import com.cropdoc.app.viewmodel.CropDocViewModel
import com.cropdoc.app.viewmodel.FarmViewModel
import com.cropdoc.app.viewmodel.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: CropDocViewModel,
    weatherViewModel: WeatherViewModel,
    farmViewModel: FarmViewModel,
    onNavigateToCamera: () -> Unit,
    onNavigateToSensor: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToChat: () -> Unit,
    onNavigateToFarmMap: () -> Unit,
    onNavigateToWeather: () -> Unit,
    onOpenLanguagePicker: () -> Unit
) {
    val modelState by viewModel.modelState.collectAsState()
    val bleState by viewModel.bleState.collectAsState()
    val soilReading by viewModel.soilReading.collectAsState()
    val latestWeather by weatherViewModel.latestWeather.collectAsState(initial = null)
    val weatherProfile by weatherViewModel.weatherProfile.collectAsState(initial = null)
    val allZones by farmViewModel.allZones.collectAsState(initial = emptyList())
    val context = LocalContext.current
    val agentActive = remember {
        mutableStateOf(
            context.getSharedPreferences("cropdoc", Context.MODE_PRIVATE)
                .getBoolean("agent_active", false)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Eco,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            stringResource(R.string.app_name),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(onClick = onOpenLanguagePicker) {
                        Icon(
                            Icons.Default.Language,
                            contentDescription = stringResource(R.string.language_select),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = stringResource(R.string.cd_history),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.home_greeting),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = stringResource(R.string.home_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ModelStatusCard(modelState, viewModel::retryEngineLoad)

                BleBanner(bleState)

                WeatherCard(
                    weather = latestWeather,
                    isOptedIn = weatherProfile != null,
                    onSetup = onNavigateToWeather
                )

                AgentModeCard(
                    isActive = agentActive.value,
                    onToggle = { active ->
                        agentActive.value = active
                        val prefs = context.getSharedPreferences("cropdoc", Context.MODE_PRIVATE)
                        val editor = prefs.edit()
                        editor.putBoolean("agent_active", active)
                        editor.commit()
                        if (active) {
                            CropDocApplication.instance.scheduleAgent()
                        } else {
                            CropDocApplication.instance.cancelAgent()
                        }
                    }
                )

                AnimatedVisibility(
                    visible = soilReading != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    soilReading?.let { reading ->
                        SoilReadingPanel(reading)
                    }
                }

                FarmMapPreview(
                    zones = allZones,
                    onNavigateToFarmMap = onNavigateToFarmMap
                )

                ChatButton(onClick = onNavigateToChat)

                Text(
                    text = stringResource(R.string.home_what_to_do),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ActionCard(
                        icon = Icons.Default.CameraAlt,
                        title = stringResource(R.string.action_scan_crop),
                        subtitle = stringResource(R.string.action_scan_crop_subtitle),
                        color = MaterialTheme.colorScheme.primary,
                        enabled = modelState is ModelState.Ready,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToCamera
                    )
                    ActionCard(
                        icon = Icons.Default.Sensors,
                        title = stringResource(R.string.action_soil_sensor),
                        subtitle = stringResource(R.string.action_soil_sensor_subtitle),
                        color = Color(0xFF795548),
                        enabled = true,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToSensor
                    )
                }

                AnimatedVisibility(
                    visible = soilReading != null && modelState is ModelState.Ready
                ) {
                    Button(
                        onClick = { viewModel.analyseSoilOnly() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Science, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.action_analyse_soil))
                    }
                }
            }
        }
    }
}

// ── Weather Card ──────────────────────────────────────────────────────────────

@Composable
private fun WeatherCard(
    weather: WeatherData?,
    isOptedIn: Boolean,
    onSetup: () -> Unit
) {
    if (!isOptedIn) {
        Card(
            onClick = onSetup,
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(Icons.Default.WbCloudy, null, tint = Color(0xFF1976D2), modifier = Modifier.size(28.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        stringResource(R.string.weather_enable),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1565C0)
                    )
                    Text(
                        stringResource(R.string.weather_enable_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF1976D2)
                    )
                }
                Icon(Icons.Default.ChevronRight, null, tint = Color(0xFF1976D2))
            }
        }
        return
    }

    if (weather == null) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp, color = Color(0xFF1976D2))
                Text(
                    stringResource(R.string.weather_waiting),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF1565C0)
                )
            }
        }
        return
    }

    Card(
        onClick = onSetup,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.WbSunny, null, tint = Color(0xFF1976D2), modifier = Modifier.size(18.dp))
                Text(
                    "${stringResource(R.string.weather_title)} — ${weather.location}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1565C0)
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherStat("🌡️", "${weather.temperature}°C", stringResource(R.string.weather_stat_temp))
                WeatherStat("💧", "${weather.humidity}%", stringResource(R.string.weather_stat_humidity))
                WeatherStat("🌧️", "${weather.rainfall}mm", stringResource(R.string.weather_stat_rain))
                WeatherStat("💨", "${weather.windSpeed}km/h", stringResource(R.string.weather_stat_wind))
            }
            Spacer(Modifier.height(6.dp))
            Text(weather.forecast, style = MaterialTheme.typography.bodySmall, color = Color(0xFF1565C0))
        }
    }
}

@Composable
private fun WeatherStat(emoji: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF1976D2))
    }
}

// ── Farm Map Preview ──────────────────────────────────────────────────────────

@Composable
private fun FarmMapPreview(
    zones: List<FarmZone>,
    onNavigateToFarmMap: () -> Unit
) {
    Card(
        onClick = onNavigateToFarmMap,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Default.Map, null, tint = Color(0xFF388E3C), modifier = Modifier.size(28.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.farm_map_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1B5E20)
                )
                Text(
                    if (zones.isEmpty()) stringResource(R.string.farm_map_empty_subtitle)
                    else stringResource(R.string.farm_map_legend, zones.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF388E3C)
                )
                if (zones.isNotEmpty()) {
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        zones.take(3).forEach { zone ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(zone.color.toInt()).copy(alpha = 0.2f)
                            ) {
                                Text(
                                    zone.name,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(zone.color.toInt())
                                )
                            }
                        }
                        if (zones.size > 3) {
                            Text(
                                "+${zones.size - 3} more",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF388E3C)
                            )
                        }
                    }
                }
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color(0xFF388E3C))
        }
    }
}

// ── Chat Button ───────────────────────────────────────────────────────────────

@Composable
private fun ChatButton(onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Default.Chat, null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(28.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.chat_button_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    stringResource(R.string.chat_button_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                )
            }
            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}

// ── Model Status Card ─────────────────────────────────────────────────────────

@Composable
private fun ModelStatusCard(state: ModelState, onRetry: () -> Unit) {
    when (state) {
        ModelState.NotLoaded -> {}
        ModelState.Loading -> {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                    Text(
                        stringResource(R.string.model_loading),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
        ModelState.Ready -> {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                    Text(
                        stringResource(R.string.model_ready),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF1B5E20)
                    )
                }
            }
        }
        is ModelState.Error -> {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.Error, null, tint = Color(0xFFD32F2F), modifier = Modifier.size(18.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            stringResource(R.string.model_error),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFFB71C1C),
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(state.message, style = MaterialTheme.typography.bodySmall, color = Color(0xFFD32F2F))
                    }
                    TextButton(onClick = onRetry) {
                        Text(stringResource(R.string.model_retry))
                    }
                }
            }
        }
    }
}

// ── Action Card ───────────────────────────────────────────────────────────────

@Composable
private fun ActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(140.dp),
        shape = RoundedCornerShape(18.dp),
        enabled = enabled,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = if (enabled) 1f else 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (enabled) 4.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.White)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.85f))
            }
        }
    }
}

// ── Tips Card ─────────────────────────────────────────────────────────────────


// ── Agent Card ─────────────────────────────────────────────────────────────────
@Composable
private fun AgentModeCard(isActive: Boolean, onToggle: (Boolean) -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                if (isActive) Icons.Default.SmartToy else Icons.Default.SmartToy,
                null,
                tint = if (isActive) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp)
            )
            Column(Modifier.weight(1f)) {
                Text(
                    "Agentic Mode",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isActive) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    if (isActive) "Active — checking farm conditions twice daily"
                    else "Off — tap to enable automatic farm monitoring",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isActive) Color(0xFF388E3C) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = isActive,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF2E7D32),
                    checkedTrackColor = Color(0xFF81C784)
                )
            )
        }
    }
}