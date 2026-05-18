package com.cropdoc.app.ui.screens

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.cropdoc.app.CropDocApplication
import com.cropdoc.app.R
import com.cropdoc.app.data.model.WeatherData
import com.cropdoc.app.data.repository.WeatherRepository
import com.cropdoc.app.data.sms.WeatherSmsReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import com.cropdoc.app.viewmodel.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherOptInScreen(
    viewModel: WeatherViewModel,
    onNavigateBack: () -> Unit
) {
    val weatherProfile by viewModel.weatherProfile.collectAsState(initial = null)
    val saveState by viewModel.saveState.collectAsState()
    val latestWeather by viewModel.latestWeather.collectAsState(initial = null)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var phoneNumber by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var showOptOutDialog by remember { mutableStateOf(false) }
    var syncState by remember { mutableStateOf<String?>(null) }
    var isSyncing by remember { mutableStateOf(false) }

    LaunchedEffect(weatherProfile) {
        weatherProfile?.let {
            phoneNumber = it.phoneNumber
            location = it.location
        }
    }

    LaunchedEffect(saveState) {
        if (saveState is WeatherViewModel.SaveState.Success) {
            viewModel.resetSaveState()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.weather_title),
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.WbSunny, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(40.dp))
                    Column {
                        Text(
                            stringResource(R.string.weather_enable),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            stringResource(R.string.weather_enable_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Current weather if available
            latestWeather?.let { weather ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Cloud, null, tint = Color(0xFF1976D2), modifier = Modifier.size(20.dp))
                            Text("${stringResource(R.string.weather_title)} — ${weather.location}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Color(0xFF1565C0))
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            WeatherStat("🌡️", "${weather.temperature}°C", stringResource(R.string.weather_stat_temp))
                            WeatherStat("💧", "${weather.humidity}%", stringResource(R.string.weather_stat_humidity))
                            WeatherStat("🌧️", "${weather.rainfall}mm", stringResource(R.string.weather_stat_rain))
                            WeatherStat("💨", "${weather.windSpeed}km/h", stringResource(R.string.weather_stat_wind))
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(weather.forecast, style = MaterialTheme.typography.bodySmall, color = Color(0xFF1565C0))
                    }
                }
            }

            // Opted in status
            if (weatherProfile != null) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                        Text(stringResource(R.string.weather_opted_in), style = MaterialTheme.typography.bodyMedium, color = Color(0xFF1B5E20), modifier = Modifier.weight(1f))
                    }
                }
            }

            // ── Sync Weather button ───────────────────────────────────────────
            // Reads FARMASSISTANT_WEATHER SMS directly from inbox instead of
            // relying on the broadcast receiver which is blocked on Android 15
            // and Motorola devices.
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Sync, null, tint = Color(0xFF1976D2), modifier = Modifier.size(18.dp))
                        Text(
                            "Sync Weather from SMS",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1565C0)
                        )
                    }
                    Text(
                        "Tap to read the latest FARMASSISTANT_WEATHER message from your inbox and update the weather display.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF1976D2)
                    )

                    syncState?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (it.startsWith("✓")) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                        )
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                isSyncing = true
                                syncState = null
                                val result = syncWeatherFromInbox(context)
                                syncState = result
                                isSyncing = false
                            }
                        },
                        enabled = !isSyncing,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1976D2)
                        )
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text("Syncing…", color = Color.White)
                        } else {
                            Icon(Icons.Default.Sync, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Sync Now", color = Color.White)
                        }
                    }
                }
            }

            // Form title
            Text(
                if (weatherProfile != null) stringResource(R.string.weather_update_details)
                else stringResource(R.string.weather_enable),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text(stringResource(R.string.weather_phone_label)) },
                placeholder = { Text(stringResource(R.string.weather_phone_hint)) },
                leadingIcon = { Icon(Icons.Default.Phone, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text(stringResource(R.string.weather_location_label)) },
                placeholder = { Text(stringResource(R.string.weather_location_hint)) },
                leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Error message
            AnimatedVisibility(visible = saveState is WeatherViewModel.SaveState.Error) {
                val message = (saveState as? WeatherViewModel.SaveState.Error)?.message ?: ""
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))) {
                    Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Error, null, tint = Color(0xFFD32F2F))
                        Text(message, color = Color(0xFFD32F2F))
                    }
                }
            }

            // Save button
            Button(
                onClick = { viewModel.saveProfile(phoneNumber, location) },
                enabled = saveState !is WeatherViewModel.SaveState.Saving,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (saveState is WeatherViewModel.SaveState.Saving) {
                    CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.weather_waiting))
                } else {
                    Icon(Icons.Default.NotificationsActive, null)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (weatherProfile != null) stringResource(R.string.weather_update_details)
                        else stringResource(R.string.weather_enable)
                    )
                }
            }

            // Opt out button
            if (weatherProfile != null) {
                OutlinedButton(
                    onClick = { showOptOutDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.NotificationsOff, null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.weather_stop))
                }
            }

            // How it works
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Text(stringResource(R.string.weather_how_it_works), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.height(10.dp))
                    val steps = listOf(
                        stringResource(R.string.weather_step_1),
                        stringResource(R.string.weather_step_2),
                        stringResource(R.string.weather_step_3),
                        stringResource(R.string.weather_step_4),
                        stringResource(R.string.weather_step_5)
                    )
                    steps.forEachIndexed { i, step ->
                        Row(modifier = Modifier.padding(vertical = 3.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Surface(
                                shape = androidx.compose.foundation.shape.CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("${i + 1}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(step, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    if (showOptOutDialog) {
        AlertDialog(
            onDismissRequest = { showOptOutDialog = false },
            icon = { Icon(Icons.Default.NotificationsOff, null, tint = Color(0xFFD32F2F)) },
            title = { Text(stringResource(R.string.weather_stop_confirm_title)) },
            text = { Text(stringResource(R.string.weather_stop_confirm_body)) },
            confirmButton = {
                TextButton(onClick = { viewModel.optOut(); showOptOutDialog = false; onNavigateBack() }) {
                    Text(stringResource(R.string.weather_stop), color = Color(0xFFD32F2F))
                }
            },
            dismissButton = {
                TextButton(onClick = { showOptOutDialog = false }) { Text(stringResource(R.string.sensor_dismiss)) }
            }
        )
    }
}

// Reads FARMASSISTANT_WEATHER SMS directly from the device inbox.
// This bypasses the broadcast receiver which is blocked on Android 15
// and Motorola devices. Searches the 50 most recent messages and
// parses the first match found.
private suspend fun syncWeatherFromInbox(context: android.content.Context): String {
    android.util.Log.d("WeatherSync", "syncWeatherFromInbox called")
    return withContext(Dispatchers.IO) {
        android.util.Log.d("WeatherSync", "Inside withContext")
        try {
            val cursor = context.contentResolver.query(
                Uri.parse("content://sms"),
                arrayOf("body", "date"),
                null,
                null,
                "date DESC"
            )

            android.util.Log.d("WeatherSync", "Cursor count: ${cursor?.count ?: -1}")

            // Collect all SMS bodies first so we can join multipart messages
            val allBodies = mutableListOf<String>()
            cursor?.use {
                var count = 0
                while (it.moveToNext() && count < 100) {
                    count++
                    val body = it.getString(it.getColumnIndexOrThrow("body")) ?: continue
                    android.util.Log.d("WeatherSync", "SMS body length: ${body.length}, body: $body")
                    allBodies.add(body)
                }
            }

            // Find FARMASSISTANT_WEATHER message — join consecutive parts if split
            for (i in allBodies.indices) {
                val body = allBodies[i]
                if (!body.contains(WeatherSmsReceiver.WEATHER_TYPE)) continue

                // Start with this part — append next part if JSON looks incomplete
                var fullBody = body
                if (!fullBody.contains("}") && i + 1 < allBodies.size) {
                    fullBody = body + allBodies[i + 1]
                    android.util.Log.d("WeatherSync", "Joined multipart SMS: $fullBody")
                }

                // Strip invisible Unicode characters inserted by Google Voice
                // that corrupt JSON keys even though the body looks normal in logs
                val cleanBody = fullBody
                    .replace(Regex("[^\\x20-\\x7E]"), "")
                    .trim()

                val jsonStart = cleanBody.indexOf("{")
                val jsonEnd = cleanBody.lastIndexOf("}") + 1
                if (jsonStart == -1 || jsonEnd == 0) continue

                try {
                    val json = JSONObject(cleanBody.substring(jsonStart, jsonEnd))
                    // Type check removed — Google Voice adds invisible Unicode chars
                    // that corrupt JSON keys. Body already confirmed to contain WEATHER_TYPE.

                    val weather = WeatherData(
                        temperature = json.getDouble("temperature").toFloat(),
                        humidity    = json.getDouble("humidity").toFloat(),
                        rainfall    = json.getDouble("rainfall").toFloat(),
                        windSpeed   = json.getDouble("windSpeed").toFloat(),
                        forecast    = json.getString("forecast"),
                        location    = json.getString("location")
                    )

                    val repo = WeatherRepository(
                        CropDocApplication.instance.database.weatherDao()
                    )
                    repo.saveWeather(weather)

                    return@withContext "✓ Weather updated — ${weather.location} ${weather.temperature}°C"
                } catch (e: Exception) {
                    android.util.Log.e("WeatherSync", "Parse failed: ${e.message}")
                    continue
                }
            }

            "No FARMASSISTANT_WEATHER message found in inbox"
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}


@Composable
private fun WeatherStat(emoji: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, style = MaterialTheme.typography.titleMedium)
        Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF1976D2))
    }
}