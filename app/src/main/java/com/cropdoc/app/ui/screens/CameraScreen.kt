package com.cropdoc.app.ui.screens

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.cropdoc.app.R
import com.cropdoc.app.data.model.AnalysisState
import com.cropdoc.app.data.model.ModelState
import com.cropdoc.app.ui.components.*
import com.cropdoc.app.viewmodel.CropDocViewModel
import java.util.concurrent.Executor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    viewModel: CropDocViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val analysisState by viewModel.analysisState.collectAsState()
    val capturedUri by viewModel.capturedImageUri.collectAsState()
    val soilReading by viewModel.soilReading.collectAsState()
    val streamingText by viewModel.streamingText.collectAsState()
    val modelState by viewModel.modelState.collectAsState()

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var showCamera by remember { mutableStateOf(capturedUri == null) }
    var cameraError by remember { mutableStateOf(false) }
    var includeSoilData by remember { mutableStateOf(true) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.onImageCaptured(it)
            showCamera = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.camera_title),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.resetAnalysis()
                        viewModel.clearImage()
                        onNavigateBack()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            stringResource(R.string.cd_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    if (capturedUri != null) {
                        IconButton(onClick = {
                            viewModel.clearImage()
                            showCamera = true
                        }) {
                            Icon(
                                Icons.Default.Refresh,
                                stringResource(R.string.camera_retake),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (showCamera && capturedUri == null) {
                if (cameraError) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                stringResource(R.string.camera_not_available),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                stringResource(R.string.camera_use_gallery),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    CameraPreview(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        onImageCaptureReady = { imageCapture = it },
                        onCameraError = { cameraError = true }
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.95f))
                        .padding(vertical = 20.dp, horizontal = 32.dp)
                ) {
                    IconButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(52.dp)
                            .border(2.dp, Color.White.copy(alpha = 0.7f), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.PhotoLibrary,
                            contentDescription = "Pick from gallery",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            if (cameraError) {
                                galleryLauncher.launch("image/*")
                            } else {
                                captureImage(
                                    context = context,
                                    imageCapture = imageCapture,
                                    executor = ContextCompat.getMainExecutor(context),
                                    onImageCaptured = { uri ->
                                        viewModel.onImageCaptured(uri)
                                        showCamera = false
                                    }
                                )
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(72.dp)
                            .border(4.dp, Color.White, CircleShape)
                    ) {
                        Icon(
                            if (cameraError) Icons.Default.PhotoLibrary else Icons.Default.CameraAlt,
                            contentDescription = stringResource(R.string.camera_capture),
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            if (cameraError) stringResource(R.string.camera_gallery_only)
                            else stringResource(R.string.camera_gallery_or_camera),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    capturedUri?.let { uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = stringResource(R.string.cd_crop_image),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .height(260.dp)
                                .clip(RoundedCornerShape(20.dp))
                        )
                    }

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        when (analysisState) {
                            AnalysisState.Idle -> {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                                    // Soil opt-in toggle
                                    if (soilReading != null) {
                                        Card(
                                            shape = RoundedCornerShape(14.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (includeSoilData)
                                                    Color(0xFFE8F5E9)
                                                else
                                                    MaterialTheme.colorScheme.surfaceVariant
                                            ),
                                            border = if (includeSoilData)
                                                androidx.compose.foundation.BorderStroke(
                                                    1.5.dp, Color(0xFF43A047)
                                                )
                                            else null
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(14.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Sensors,
                                                    null,
                                                    tint = if (includeSoilData) Color(0xFF2E7D32)
                                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Column(Modifier.weight(1f)) {
                                                    Text(
                                                        stringResource(R.string.include_soil_data),
                                                        style = MaterialTheme.typography.titleSmall,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = if (includeSoilData) Color(0xFF1B5E20)
                                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                    Text(
                                                        "Moisture ${soilReading!!.moisture.toInt()}% • pH ${
                                                            "%.1f".format(soilReading!!.ph)} • N ${
                                                            soilReading!!.nitrogen.toInt()} mg/kg",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = if (includeSoilData) Color(0xFF388E3C)
                                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                Switch(
                                                    checked = includeSoilData,
                                                    onCheckedChange = { includeSoilData = it },
                                                    colors = SwitchDefaults.colors(
                                                        checkedThumbColor = Color.White,
                                                        checkedTrackColor = Color(0xFF43A047)
                                                    )
                                                )
                                            }
                                        }
                                    }

                                    Button(
                                        onClick = { viewModel.analyseCapture(includeSoil = includeSoilData) },
                                        enabled = modelState is ModelState.Ready,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(52.dp),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Icon(Icons.Default.Search, null)
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            when {
                                                soilReading != null && includeSoilData ->
                                                    stringResource(R.string.camera_analyse_with_soil)
                                                else ->
                                                    stringResource(R.string.camera_analyse)
                                            },
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                    }
                                }
                            }

                            AnalysisState.Analyzing -> {
                                AnalysingCard(streamingText)
                            }

                            is AnalysisState.Complete -> {
                                val result = (analysisState as AnalysisState.Complete).result
                                AnalysisResultCard(result)
                            }

                            is AnalysisState.Error -> {
                                val message = (analysisState as AnalysisState.Error).message
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(0xFFFFEBEE)
                                    )
                                ) {
                                    Row(
                                        Modifier.padding(14.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.Error, null, tint = Color(0xFFD32F2F))
                                        Text(message, color = Color(0xFFD32F2F))
                                    }
                                }
                                Button(
                                    onClick = { viewModel.analyseCapture() },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(stringResource(R.string.retry_analysis))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalysingCard(streamingText: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                Text(
                    stringResource(R.string.camera_analysing),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            if (streamingText.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = streamingText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun AnalysisResultCard(result: com.cropdoc.app.data.model.AnalysisResult) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HealthScoreGauge(result.overallHealthScore)
                Column(Modifier.weight(1f)) {
                    Text(
                        stringResource(R.string.analysis_complete),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        stringResource(
                            R.string.issues_found,
                            result.diseases.size,
                            result.soilRecommendations.size
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (result.diseases.isNotEmpty()) {
            SectionHeader(
                stringResource(R.string.diseases_issues),
                Icons.Default.BugReport
            )
            result.diseases.forEach { disease ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                disease.name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                stringResource(R.string.affected, disease.affectedArea),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        SeverityChip(disease.severity)
                    }
                }
            }
        }

        if (result.immediateActions.isNotEmpty()) {
            SectionHeader(
                stringResource(R.string.immediate_actions),
                Icons.Default.Warning
            )
            result.immediateActions.forEach { action ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "→",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(action, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        if (result.soilRecommendations.isNotEmpty()) {
            SectionHeader(
                stringResource(R.string.soil_recommendations),
                Icons.Default.Science
            )
            result.soilRecommendations.forEach { rec ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            .copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                rec.parameter,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            SeverityChip(rec.priority)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            stringResource(
                                R.string.current_to_target,
                                rec.currentValue,
                                rec.targetRange
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            rec.action,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
        }

        SectionHeader(
            stringResource(R.string.full_ai_analysis),
            Icons.Default.Article
        )
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = result.summary,
                modifier = Modifier.padding(14.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun CameraPreview(
    modifier: Modifier = Modifier,
    onImageCaptureReady: (ImageCapture) -> Unit,
    onCameraError: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    LaunchedEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val provider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val capture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            onImageCaptureReady(capture)

            try {
                provider.unbindAll()
                provider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    capture
                )
            } catch (e: Exception) {
                e.printStackTrace()
                onCameraError()
            }
        }, ContextCompat.getMainExecutor(context))
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    )
}

private fun captureImage(
    context: Context,
    imageCapture: ImageCapture?,
    executor: Executor,
    onImageCaptured: (android.net.Uri) -> Unit
) {
    val capture = imageCapture ?: return

    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "crop_${System.currentTimeMillis()}.jpg")
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
    }

    val outputOptions = ImageCapture.OutputFileOptions.Builder(
        context.contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    ).build()

    capture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                output.savedUri?.let(onImageCaptured)
            }
            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
            }
        }
    )
}