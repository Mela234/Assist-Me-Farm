package com.cropdoc.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cropdoc.app.R
import com.cropdoc.app.data.model.Crop
import com.cropdoc.app.data.model.FarmZone
import com.cropdoc.app.viewmodel.FarmViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmMapScreen(
    farmViewModel: FarmViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToZoneChat: (Long) -> Unit
) {
    val zones by farmViewModel.allZones.collectAsState(initial = emptyList())
    val selectedZone by farmViewModel.selectedZone.collectAsState()
    val selectedZoneCrops by farmViewModel.selectedZoneCrops.collectAsState()
    val selectedZoneLatestReading by farmViewModel.selectedZoneLatestReading.collectAsState()

    var showAddZoneDialog by remember { mutableStateOf(false) }
    var showZoneDetailSheet by remember { mutableStateOf(false) }
    var pendingZonePosition by remember { mutableStateOf(Offset.Zero) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.farm_map_title),
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
                ),
                actions = {
                    IconButton(onClick = {
                        pendingZonePosition = Offset(100f, 100f)
                        showAddZoneDialog = true
                    }) {
                        Icon(
                            Icons.Default.Add,
                            stringResource(R.string.farm_map_add_zone),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (zones.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Map,
                        null,
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        stringResource(R.string.farm_map_empty_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        stringResource(R.string.farm_map_empty_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = {
                            pendingZonePosition = Offset(100f, 100f)
                            showAddZoneDialog = true
                        },
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Add, null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.farm_map_add_first))
                    }
                }
            } else {
                FarmCanvas(
                    zones = zones,
                    onZoneTap = { zone ->
                        farmViewModel.selectZone(zone)
                        showZoneDetailSheet = true
                    },
                    onCanvasLongPress = { position ->
                        pendingZonePosition = position
                        showAddZoneDialog = true
                    },
                    onZoneMoved = { zone, newOffset ->
                        farmViewModel.updateZone(zone.copy(x = newOffset.x, y = newOffset.y))
                    }
                )
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            stringResource(R.string.farm_map_legend, zones.size),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    if (showAddZoneDialog) {
        AddZoneDialog(
            position = pendingZonePosition,
            onDismiss = { showAddZoneDialog = false },
            onConfirm = { name, color ->
                farmViewModel.addZone(
                    name = name,
                    x = pendingZonePosition.x,
                    y = pendingZonePosition.y,
                    width = 200f,
                    height = 150f,
                    color = color
                )
                showAddZoneDialog = false
            }
        )
    }

    if (showZoneDetailSheet && selectedZone != null) {
        ZoneDetailSheet(
            zone = selectedZone!!,
            crops = selectedZoneCrops,
            latestReading = selectedZoneLatestReading,
            farmViewModel = farmViewModel,
            onDismiss = {
                showZoneDetailSheet = false
                farmViewModel.clearSelectedZone()
            },
            onChatAboutZone = { zoneId ->
                showZoneDetailSheet = false
                farmViewModel.clearSelectedZone()
                onNavigateToZoneChat(zoneId)
            }
        )
    }
}

@Composable
private fun FarmCanvas(
    zones: List<FarmZone>,
    onZoneTap: (FarmZone) -> Unit,
    onCanvasLongPress: (Offset) -> Unit,
    onZoneMoved: (FarmZone, Offset) -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var draggingZoneId by remember { mutableStateOf<Long?>(null) }
    var zoneDragStart by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F8E9))
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    if (draggingZoneId == null) {
                        scale = (scale * zoom).coerceIn(0.5f, 3f)
                        offset += pan
                    }
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { tapOffset ->
                        val canvasPos = (tapOffset - offset) / scale
                        onCanvasLongPress(canvasPos)
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val gridSize = 50f * scale
            val startX = offset.x % gridSize
            val startY = offset.y % gridSize
            var x = startX
            while (x < size.width) {
                drawLine(color = Color(0xFFDCEDC8), start = Offset(x, 0f), end = Offset(x, size.height), strokeWidth = 1f)
                x += gridSize
            }
            var y = startY
            while (y < size.height) {
                drawLine(color = Color(0xFFDCEDC8), start = Offset(0f, y), end = Offset(size.width, y), strokeWidth = 1f)
                y += gridSize
            }
        }

        zones.forEach { zone ->
            val screenX = zone.x * scale + offset.x
            val screenY = zone.y * scale + offset.y
            val screenW = zone.width * scale
            val screenH = zone.height * scale

            ZoneBlock(
                zone = zone,
                x = screenX, y = screenY, width = screenW, height = screenH,
                onTap = { onZoneTap(zone) },
                onDragStart = {
                    draggingZoneId = zone.id
                    zoneDragStart = Offset(zone.x, zone.y)
                },
                onDrag = { dragAmount ->
                    if (draggingZoneId == zone.id) {
                        val newX = zoneDragStart.x + dragAmount.x / scale
                        val newY = zoneDragStart.y + dragAmount.y / scale
                        onZoneMoved(zone, Offset(newX, newY))
                    }
                },
                onDragEnd = { draggingZoneId = null }
            )
        }
    }
}

@Composable
private fun ZoneBlock(
    zone: FarmZone,
    x: Float, y: Float, width: Float, height: Float,
    onTap: () -> Unit,
    onDragStart: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit
) {
    val zoneColor = Color(zone.color.toInt())
    var totalDrag by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .offset(x = x.dp, y = y.dp)
            .size(width = width.dp, height = height.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(zoneColor.copy(alpha = 0.3f))
            .border(
                width = if (zone.isActiveForSensor) 3.dp else 1.5.dp,
                color = if (zone.isActiveForSensor) Color(0xFF43A047) else zoneColor,
                shape = RoundedCornerShape(8.dp)
            )
            .pointerInput(zone.id.toString() + "_tap") {
                detectTapGestures(onTap = { onTap() })
            }
            .pointerInput(zone.id.toString() + "_drag") {
                detectDragGestures(
                    onDragStart = { offset ->
                        totalDrag = Offset.Zero
                        onDragStart(offset)
                    },
                    onDrag = { _, dragAmount ->
                        totalDrag += dragAmount
                        onDrag(totalDrag)
                    },
                    onDragEnd = { onDragEnd() },
                    onDragCancel = { onDragEnd() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                zone.name,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = zoneColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (zone.isActiveForSensor) {
                Spacer(Modifier.height(4.dp))
                Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF43A047)) {
                    Text(
                        stringResource(R.string.farm_map_active_sensor_zone),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun AddZoneDialog(
    position: Offset,
    onDismiss: () -> Unit,
    onConfirm: (String, Long) -> Unit
) {
    var zoneName by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(0xFF4CAF50L) }

    val colorOptions = listOf(
        0xFF4CAF50L, 0xFF2196F3L, 0xFFFF9800L, 0xFF9C27B0L,
        0xFFF44336L, 0xFF795548L, 0xFF00BCD4L, 0xFFFFEB3BL,
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.primary) },
        title = { Text(stringResource(R.string.farm_map_add_zone)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = zoneName,
                    onValueChange = { zoneName = it },
                    label = { Text(stringResource(R.string.farm_map_zone_name_label)) },
                    placeholder = { Text(stringResource(R.string.farm_map_zone_name_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Text(
                    stringResource(R.string.farm_map_zone_color),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colorOptions.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(color.toInt()))
                                .border(
                                    width = if (selectedColor == color) 3.dp else 0.dp,
                                    color = Color.White,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = color }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (zoneName.isNotBlank()) onConfirm(zoneName, selectedColor) },
                enabled = zoneName.isNotBlank()
            ) { Text(stringResource(R.string.farm_map_add_zone)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.sensor_dismiss)) }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ZoneDetailSheet(
    zone: FarmZone,
    crops: List<Crop>,
    latestReading: com.cropdoc.app.data.model.SoilReadingHistory?,
    farmViewModel: FarmViewModel,
    onDismiss: () -> Unit,
    onChatAboutZone: (Long) -> Unit
) {
    var showAddCropDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val zoneColor = Color(zone.color.toInt())

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(zoneColor.copy(alpha = 0.2f))
                        .border(2.dp, zoneColor, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Grass, null, tint = zoneColor, modifier = Modifier.size(24.dp))
                }
                Column(Modifier.weight(1f)) {
                    Text(zone.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    if (zone.isActiveForSensor) {
                        Text(
                            stringResource(R.string.farm_map_active_sensor_zone),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF43A047)
                        )
                    }
                }
            }

            HorizontalDivider()

            Text(stringResource(R.string.farm_map_crops_title), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)

            if (crops.isEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        stringResource(R.string.farm_map_no_crops),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedButton(onClick = { showAddCropDialog = true }, shape = RoundedCornerShape(10.dp)) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.farm_map_add_crop))
                    }
                }
            } else {
                crops.forEach { crop -> CropCard(crop = crop, farmViewModel = farmViewModel) }
                OutlinedButton(
                    onClick = { showAddCropDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.farm_map_add_another_crop))
                }
            }

            HorizontalDivider()

            Text(stringResource(R.string.farm_map_soil_title), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)

            if (latestReading == null) {
                Text(
                    stringResource(R.string.farm_map_no_readings),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                val dateFormat = SimpleDateFormat("dd MMM HH:mm", Locale.getDefault())
                Text(
                    stringResource(R.string.farm_map_last_updated, dateFormat.format(Date(latestReading.timestamp))),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    MiniReadingStat("💧", "${latestReading.moisture.toInt()}%", stringResource(R.string.soil_moisture))
                    MiniReadingStat("🧪", "%.1f".format(latestReading.ph), stringResource(R.string.soil_ph))
                    MiniReadingStat("🌱", "${latestReading.nitrogen.toInt()}", stringResource(R.string.soil_nitrogen))
                    MiniReadingStat("🌿", "${latestReading.phosphorus.toInt()}", stringResource(R.string.soil_phosphorus))
                    MiniReadingStat("🍃", "${latestReading.potassium.toInt()}", stringResource(R.string.soil_potassium))
                }
            }

            HorizontalDivider()

            if (!zone.isActiveForSensor) {
                OutlinedButton(
                    onClick = { farmViewModel.setActiveZone(zone.id); onDismiss() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Sensors, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.farm_map_set_active_sensor))
                }
            }

            Button(
                onClick = { onChatAboutZone(zone.id) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Chat, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.farm_map_chat_about_zone))
            }

            OutlinedButton(
                onClick = { showDeleteConfirm = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F))
            ) {
                Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.farm_map_delete_zone))
            }
        }
    }

    if (showAddCropDialog) {
        AddCropDialog(
            zoneId = zone.id,
            onDismiss = { showAddCropDialog = false },
            onConfirm = { name, plantedDate, harvestDays, notes ->
                farmViewModel.addCrop(zoneId = zone.id, name = name, plantedDate = plantedDate, expectedHarvestDays = harvestDays, notes = notes)
                showAddCropDialog = false
            }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            icon = { Icon(Icons.Default.Delete, null, tint = Color(0xFFD32F2F)) },
            title = { Text(stringResource(R.string.farm_map_delete_confirm_title, zone.name)) },
            text = { Text(stringResource(R.string.farm_map_delete_confirm_body)) },
            confirmButton = {
                TextButton(onClick = { farmViewModel.deleteZone(zone); showDeleteConfirm = false; onDismiss() }) {
                    Text(stringResource(R.string.farm_map_delete_zone), color = Color(0xFFD32F2F))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text(stringResource(R.string.sensor_dismiss)) }
            }
        )
    }
}

@Composable
private fun CropCard(crop: Crop, farmViewModel: FarmViewModel) {
    val daysPlanted = farmViewModel.getDaysPlanted(crop.plantedDate)
    val daysToHarvest = farmViewModel.getDaysToHarvest(crop.plantedDate, crop.expectedHarvestDays)
    val progress = (daysPlanted.toFloat() / crop.expectedHarvestDays).coerceIn(0f, 1f)

    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("🌾 ${crop.name}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(
                    if (daysToHarvest == 0) stringResource(R.string.farm_map_ready_to_harvest)
                    else stringResource(R.string.farm_map_days_to_harvest, daysToHarvest),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (daysToHarvest == 0) Color(0xFF43A047) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
                color = when {
                    progress >= 1f -> Color(0xFF43A047)
                    progress >= 0.7f -> Color(0xFFFFA000)
                    else -> MaterialTheme.colorScheme.primary
                },
                trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                stringResource(R.string.farm_map_day_progress, daysPlanted, crop.expectedHarvestDays),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (crop.notes.isNotBlank()) {
                Text(crop.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
private fun MiniReadingStat(emoji: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 16.sp)
        Text(value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp)
    }
}

@Composable
private fun AddCropDialog(
    zoneId: Long,
    onDismiss: () -> Unit,
    onConfirm: (String, Long, Int, String) -> Unit
) {
    var cropName by remember { mutableStateOf("") }
    var harvestDays by remember { mutableStateOf("90") }
    var notes by remember { mutableStateOf("") }
    val plantedDate = System.currentTimeMillis()

    val commonCrops = listOf("Maize", "Tomatoes", "Beans", "Groundnuts", "Sorghum", "Tobacco", "Cotton", "Wheat")

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Grass, null, tint = MaterialTheme.colorScheme.primary) },
        title = { Text(stringResource(R.string.farm_map_add_crop_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = cropName, onValueChange = { cropName = it },
                    label = { Text(stringResource(R.string.farm_map_crop_name_label)) },
                    placeholder = { Text(stringResource(R.string.farm_map_crop_name_hint)) },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true
                )
                Text(stringResource(R.string.farm_map_quick_select), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    commonCrops.forEach { crop ->
                        FilterChip(selected = cropName == crop, onClick = { cropName = crop }, label = { Text(crop, style = MaterialTheme.typography.labelSmall) })
                    }
                }
                OutlinedTextField(
                    value = harvestDays, onValueChange = { harvestDays = it.filter { c -> c.isDigit() } },
                    label = { Text(stringResource(R.string.farm_map_harvest_days_label)) },
                    placeholder = { Text("90") },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true
                )
                OutlinedTextField(
                    value = notes, onValueChange = { notes = it },
                    label = { Text(stringResource(R.string.farm_map_notes_label)) },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (cropName.isNotBlank()) onConfirm(cropName, plantedDate, harvestDays.toIntOrNull() ?: 90, notes) },
                enabled = cropName.isNotBlank()
            ) { Text(stringResource(R.string.farm_map_add_crop)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.sensor_dismiss)) } }
    )
}