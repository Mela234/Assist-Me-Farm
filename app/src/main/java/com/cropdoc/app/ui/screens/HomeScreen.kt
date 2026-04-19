package com.cropdoc.app.ui.screens

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cropdoc.app.data.model.BleState
import com.cropdoc.app.data.model.ModelState
import com.cropdoc.app.ui.components.BleBanner
import com.cropdoc.app.ui.components.SoilReadingPanel
import com.cropdoc.app.viewmodel.CropDocViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: CropDocViewModel,
    onNavigateToCamera: () -> Unit,
    onNavigateToSensor: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    val modelState by viewModel.modelState.collectAsState()
    val bleState by viewModel.bleState.collectAsState()
    val soilReading by viewModel.soilReading.collectAsState()

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
                            "CropDoc",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = "History",
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
            // Hero gradient banner
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
                        text = "Good day, Farmer 🌱",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "All analysis runs privately on your phone",
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

                // Model status
                ModelStatusCard(modelState, viewModel::retryEngineLoad)

                // BLE status
                BleBanner(bleState)

                // Soil data (if connected)
                AnimatedVisibility(
                    visible = soilReading != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    soilReading?.let { reading ->
                        SoilReadingPanel(reading)
                    }
                }

                // Main action buttons
                Text(
                    text = "What would you like to do?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Scan crop
                    ActionCard(
                        icon = Icons.Default.CameraAlt,
                        title = "Scan Crop",
                        subtitle = "Take a photo to diagnose disease",
                        color = MaterialTheme.colorScheme.primary,
                        enabled = modelState is ModelState.Ready,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToCamera
                    )
                    // Sensor
                    ActionCard(
                        icon = Icons.Default.Sensors,
                        title = "Soil Sensor",
                        subtitle = "Connect & view soil data",
                        color = Color(0xFF795548),
                        enabled = true,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToSensor
                    )
                }

                // Analyse soil only (if sensor connected + model ready)
                AnimatedVisibility(
                    visible = soilReading != null && modelState is ModelState.Ready
                ) {
                    Button(
                        onClick = { /* handled in parent nav */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Science, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Analyse Soil Data Only")
                    }
                }

                // Tips
                TipsCard()
            }
        }
    }
}

@Composable
private fun ModelStatusCard(state: ModelState, onRetry: () -> Unit) {
    when (state) {
        ModelState.NotLoaded -> {}
        ModelState.Loading -> {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                    Text(
                        "Loading Gemma 4 AI model…",
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
                    Icon(Icons.Default.CheckCircle, contentDescription = null,
                        tint = Color(0xFF2E7D32), modifier = Modifier.size(18.dp))
                    Text(
                        "AI model ready • Gemma 4 E4B",
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
                    Icon(Icons.Default.Error, contentDescription = null,
                        tint = Color(0xFFD32F2F), modifier = Modifier.size(18.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Model failed to load", style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFFB71C1C), fontWeight = FontWeight.SemiBold)
                        Text(state.message, style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFD32F2F))
                    }
                    TextButton(onClick = onRetry) { Text("Retry") }
                }
            }
        }
    }
}

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
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}

@Composable
private fun TipsCard() {
    val tips = listOf(
        "💡 Take photos in good natural light for best results",
        "📏 Get close enough to see leaf detail clearly",
        "🔄 Re-analyse after applying treatments to track progress",
        "🌡️ Soil readings update automatically when sensor is connected"
    )
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Tips", style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            tips.forEach { tip ->
                Text(
                    text = tip,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}
