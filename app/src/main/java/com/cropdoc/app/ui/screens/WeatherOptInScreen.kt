package com.cropdoc.app.ui.screens

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.cropdoc.app.R
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

    var phoneNumber by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var showOptOutDialog by remember { mutableStateOf(false) }

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

@Composable
private fun WeatherStat(emoji: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, style = MaterialTheme.typography.titleMedium)
        Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF1976D2))
    }
}