package com.cropdoc.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cropdoc.app.viewmodel.ModelDownloadViewModel

@Composable
fun ModelDownloadScreen(
    viewModel: ModelDownloadViewModel,
    onDownloadComplete: () -> Unit
) {
    val downloadState by viewModel.downloadState.collectAsState()

    // Navigate when done
    LaunchedEffect(downloadState) {
        if (downloadState is ModelDownloadViewModel.DownloadState.AlreadyExists ||
            downloadState is ModelDownloadViewModel.DownloadState.Success) {
            onDownloadComplete()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1B5E20),
                        Color(0xFF2E7D32),
                        Color(0xFF388E3C)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            // Logo / Icon
            Icon(
                Icons.Default.Eco,
                null,
                modifier = Modifier.size(72.dp),
                tint = Color.White
            )

            Text(
                "Farm Assistant",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                "AI-powered farming assistant\nfor smallholder farmers",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            // State card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.15f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (val state = downloadState) {

                        is ModelDownloadViewModel.DownloadState.Checking -> {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                "Checking for AI model…",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        is ModelDownloadViewModel.DownloadState.Idle -> {
                            Icon(
                                Icons.Default.Download,
                                null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                "AI Model Required",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Farm Assistant needs to download the Gemma 4 AI model (2.5GB) to analyse crops and give farming advice — all offline after download.",
                                color = Color.White.copy(alpha = 0.85f),
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )

                            // WiFi warning
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFFFF9C4).copy(alpha = 0.2f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Wifi,
                                        null,
                                        tint = Color(0xFFFFEB3B),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        "Download over WiFi recommended",
                                        color = Color(0xFFFFEB3B),
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }

                            Button(
                                onClick = { viewModel.startDownload() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Color(0xFF2E7D32)
                                )
                            ) {
                                Icon(Icons.Default.Download, null)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Download AI Model (2.5GB)",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        is ModelDownloadViewModel.DownloadState.Downloading -> {
                            Icon(
                                Icons.Default.CloudDownload,
                                null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                "Downloading Gemma 4…",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            // Progress bar
                            LinearProgressIndicator(
                                progress = { state.progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                color = Color.White,
                                trackColor = Color.White.copy(alpha = 0.3f)
                            )

                            Text(
                                "${"%.0f".format(state.progress * 100)}%  •  ${"%.0f".format(state.downloadedMb)} MB / ${"%.0f".format(state.totalMb)} MB",
                                color = Color.White.copy(alpha = 0.85f),
                                style = MaterialTheme.typography.labelMedium
                            )

                            Text(
                                "Keep the app open. You only need to do this once.",
                                color = Color.White.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )

                            TextButton(
                                onClick = { viewModel.cancelDownload() }
                            ) {
                                Text("Cancel", color = Color.White.copy(alpha = 0.7f))
                            }
                        }

                        is ModelDownloadViewModel.DownloadState.Processing -> {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                "Preparing AI model…",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        is ModelDownloadViewModel.DownloadState.Success -> {
                            Icon(
                                Icons.Default.CheckCircle,
                                null,
                                tint = Color(0xFF81C784),
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                "AI Model Ready!",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Gemma 4 is installed. Farm Assistant is ready to use offline.",
                                color = Color.White.copy(alpha = 0.85f),
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        }

                        is ModelDownloadViewModel.DownloadState.Error -> {
                            Icon(
                                Icons.Default.ErrorOutline,
                                null,
                                tint = Color(0xFFEF9A9A),
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                "Download Failed",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                state.message,
                                color = Color.White.copy(alpha = 0.7f),
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = { viewModel.retry() },
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = Color(0xFF2E7D32)
                                )
                            ) {
                                Icon(Icons.Default.Refresh, null)
                                Spacer(Modifier.width(8.dp))
                                Text("Try Again", fontWeight = FontWeight.Bold)
                            }
                        }

                        else -> {}
                    }
                }
            }

            // Features preview
            if (downloadState is ModelDownloadViewModel.DownloadState.Idle) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FeatureRow("🌿", "Diagnose crop diseases from photos")
                    FeatureRow("🌡️", "Analyse soil sensor readings")
                    FeatureRow("🌦️", "Get weather-aware farming advice")
                    FeatureRow("💬", "Chat with your personal farm assistant")
                    FeatureRow("📡", "Works completely offline")
                }
            }
        }
    }
}

@Composable
private fun FeatureRow(emoji: String, text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(emoji, fontSize = 20.sp)
        Text(
            text,
            color = Color.White.copy(alpha = 0.85f),
            style = MaterialTheme.typography.bodySmall
        )
    }
}