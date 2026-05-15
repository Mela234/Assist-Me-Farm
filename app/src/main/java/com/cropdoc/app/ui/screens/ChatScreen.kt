package com.cropdoc.app.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.cropdoc.app.R
import com.cropdoc.app.data.model.ChatMessage
import com.cropdoc.app.viewmodel.ChatViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.clickable

// ---------------------------------------------------------------------------
// Zero-dependency inline markdown renderer
// Handles: **bold**, *italic*, `code`, # headings, numbered lists, bullets
// ---------------------------------------------------------------------------
@Composable
private fun MarkdownText(
    markdown: String,
    color: Color,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        markdown.lines().forEach { line ->
            val trimmed = line.trimStart()

            val headingMatch = Regex("^(#{1,3})\\s+(.*)").find(trimmed)
            if (headingMatch != null) {
                val level = headingMatch.groupValues[1].length
                val text  = headingMatch.groupValues[2]
                val hStyle = when (level) {
                    1    -> style.copy(fontWeight = FontWeight.Bold, fontSize = style.fontSize * 1.3f)
                    2    -> style.copy(fontWeight = FontWeight.Bold, fontSize = style.fontSize * 1.15f)
                    else -> style.copy(fontWeight = FontWeight.Bold)
                }
                Text(text = buildInlineMarkdown(text, color), style = hStyle, color = color)
                return@forEach
            }

            val numberedMatch = Regex("^(\\d+)\\.\\s+(.*)").find(trimmed)
            if (numberedMatch != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("${numberedMatch.groupValues[1]}.", style = style,
                        color = color, fontWeight = FontWeight.SemiBold)
                    Text(text = buildInlineMarkdown(numberedMatch.groupValues[2], color),
                        style = style, color = color)
                }
                return@forEach
            }

            val bulletMatch = Regex("^[-*]\\s+(.*)").find(trimmed)
            if (bulletMatch != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("•", style = style, color = color)
                    Text(text = buildInlineMarkdown(bulletMatch.groupValues[1], color),
                        style = style, color = color)
                }
                return@forEach
            }

            if (trimmed.isEmpty()) { Spacer(Modifier.height(4.dp)); return@forEach }

            Text(text = buildInlineMarkdown(trimmed, color), style = style, color = color)
        }
    }
}

private fun buildInlineMarkdown(text: String, baseColor: Color) = buildAnnotatedString {
    data class Span(val start: Int, val end: Int, val inner: String, val type: String)

    val spans = mutableListOf<Span>()
    Regex("\\*\\*\\*(.+?)\\*\\*\\*").findAll(text).forEach {
        spans += Span(it.range.first, it.range.last + 1, it.groupValues[1], "bolditalic")
    }
    Regex("\\*\\*(.+?)\\*\\*").findAll(text).forEach {
        spans += Span(it.range.first, it.range.last + 1, it.groupValues[1], "bold")
    }
    Regex("\\*(.+?)\\*").findAll(text).forEach {
        spans += Span(it.range.first, it.range.last + 1, it.groupValues[1], "italic")
    }
    Regex("`(.+?)`").findAll(text).forEach {
        spans += Span(it.range.first, it.range.last + 1, it.groupValues[1], "code")
    }

    val filtered = mutableListOf<Span>()
    var cursor = 0
    for (span in spans.sortedWith(compareBy({ it.start }, { -(it.end - it.start) }))) {
        if (span.start >= cursor) { filtered += span; cursor = span.end }
    }

    var pos = 0
    for (span in filtered) {
        if (pos < span.start) append(text.substring(pos, span.start))
        when (span.type) {
            "bolditalic" -> { pushStyle(SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)); append(span.inner); pop() }
            "bold"       -> { pushStyle(SpanStyle(fontWeight = FontWeight.Bold)); append(span.inner); pop() }
            "italic"     -> { pushStyle(SpanStyle(fontStyle = FontStyle.Italic)); append(span.inner); pop() }
            "code"       -> { pushStyle(SpanStyle(fontFamily = FontFamily.Monospace, background = baseColor.copy(alpha = 0.15f))); append(span.inner); pop() }
        }
        pos = span.end
    }
    if (pos < text.length) append(text.substring(pos))
}

// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onNavigateBack: () -> Unit
) {
    val messages by viewModel.messages.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()
    val streamingText by viewModel.streamingText.collectAsState()
    val attachedImageUri by viewModel.attachedImageUri.collectAsState()
    val audioFile by viewModel.audioFile.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()
    val recordingSeconds by viewModel.recordingSeconds.collectAsState()
    val currentZone by viewModel.currentZone.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var showAttachMenu by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { _ -> }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.attachImage(it) }
        showAttachMenu = false
    }

    LaunchedEffect(messages.size, isTyping) {
        if (messages.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(
                    if (isTyping) messages.size else messages.size - 1
                )
            }
        }
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            currentZone?.let { "Chat — ${it.name}" }
                                ?: stringResource(R.string.chat_title),
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            stringResource(R.string.chat_powered_by),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack,
                            stringResource(R.string.cd_back),
                            tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    IconButton(onClick = { viewModel.clearChat() }) {
                        Icon(Icons.Default.DeleteOutline, stringResource(R.string.chat_clear),
                            tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        },
        bottomBar = {
            ChatInputBar(
                inputText = inputText,
                onInputChange = { inputText = it },
                attachedImageUri = attachedImageUri?.toString(),
                audioFile = audioFile,
                isRecording = isRecording,
                recordingSeconds = recordingSeconds,
                isTyping = isTyping,
                onSend = {
                    if (inputText.isNotBlank() || attachedImageUri != null || audioFile != null) {
                        viewModel.sendMessage(inputText)
                        inputText = ""
                    }
                },
                onPlusClick = { showAttachMenu = true },
                onRemoveAttachment = { viewModel.clearAttachment() },
                onRemoveAudio = { viewModel.clearAudio() },
                onStopRecording = { viewModel.stopRecording() }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (messages.isEmpty() && !isTyping) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(Icons.Default.Agriculture, null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary)
                        Text(
                            currentZone?.let { stringResource(R.string.chat_zone_title, it.name) }
                                ?: stringResource(R.string.chat_empty_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            stringResource(R.string.chat_empty_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                        Spacer(Modifier.height(8.dp))
                        val suggestions = if (currentZone != null) listOf(
                            stringResource(R.string.chat_zone_suggestion_1),
                            stringResource(R.string.chat_zone_suggestion_2),
                            stringResource(R.string.chat_zone_suggestion_3),
                            stringResource(R.string.chat_zone_suggestion_4)
                        ) else listOf(
                            stringResource(R.string.chat_suggestion_1),
                            stringResource(R.string.chat_suggestion_2),
                            stringResource(R.string.chat_suggestion_3),
                            stringResource(R.string.chat_suggestion_4)
                        )
                        suggestions.forEach { suggestion ->
                            SuggestionChip(
                                onClick = { viewModel.sendMessage(suggestion) },
                                label = { Text(suggestion) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(messages) { message -> ChatBubble(message = message) }
                    if (isTyping) {
                        item {
                            if (streamingText.isNotEmpty()) StreamingBubble(text = streamingText)
                            else TypingIndicator()
                        }
                    }
                }
            }
        }
    }

    if (showAttachMenu) {
        ModalBottomSheet(onDismissRequest = { showAttachMenu = false }) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 16.dp).padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Attach", style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
                ListItem(
                    headlineContent = { Text("Record Audio") },
                    supportingContent = { Text("Speak to Gemma — up to 30 seconds") },
                    leadingContent = {
                        Box(modifier = Modifier.size(40.dp).clip(CircleShape)
                            .background(Color(0xFFE3F2FD)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Mic, null, tint = Color(0xFF1976D2))
                        }
                    },
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { showAttachMenu = false; viewModel.startRecording() }
                )
                ListItem(
                    headlineContent = { Text("Take Photo") },
                    supportingContent = { Text("Capture crop or soil for analysis") },
                    leadingContent = {
                        Box(modifier = Modifier.size(40.dp).clip(CircleShape)
                            .background(Color(0xFFE8F5E9)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.CameraAlt, null, tint = Color(0xFF388E3C))
                        }
                    },
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { showAttachMenu = false; cameraLauncher.launch(null) }
                )
                ListItem(
                    headlineContent = { Text("Choose from Gallery") },
                    supportingContent = { Text("Select an existing photo") },
                    leadingContent = {
                        Box(modifier = Modifier.size(40.dp).clip(CircleShape)
                            .background(Color(0xFFF3E5F5)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Image, null, tint = Color(0xFF7B1FA2))
                        }
                    },
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { galleryLauncher.launch("image/*") }
                )
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isUser = message.role == "USER"
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val contentColor = if (isUser) MaterialTheme.colorScheme.onPrimary
    else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // Image attachment
        message.attachedImageUri?.let { uriStr ->
            AsyncImage(
                model = uriStr,
                contentDescription = "Attached image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .padding(bottom = 4.dp)
            )
        }

        // Audio attachment indicator
        message.audioPath?.let {
            Row(
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .background(
                        color = if (isUser) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        else Color(0xFFE3F2FD),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.GraphicEq, null,
                    tint = Color(0xFF1976D2), modifier = Modifier.size(18.dp))
                Text("Voice message",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF1565C0))
            }
        }

        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .background(
                    color = if (isUser) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(
                        topStart = 16.dp, topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd   = if (isUser) 4.dp  else 16.dp
                    )
                )
                .padding(12.dp)
        ) {
            if (isUser) {
                Text(text = message.content,
                    style = MaterialTheme.typography.bodyMedium, color = contentColor)
            } else {
                MarkdownText(markdown = message.content, color = contentColor,
                    style = MaterialTheme.typography.bodyMedium)
            }
        }

        Text(
            text = dateFormat.format(Date(message.timestamp)),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun StreamingBubble(text: String) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .background(color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp,
                        bottomStart = 4.dp, bottomEnd = 16.dp))
                .padding(12.dp)
        ) {
            Column {
                MarkdownText(markdown = text,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(4.dp))
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )
            }
        }
    }
}

@Composable
private fun TypingIndicator() {
    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp,
                    bottomStart = 4.dp, bottomEnd = 16.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator(modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(4.dp))
            Text(stringResource(R.string.chat_thinking),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ChatInputBar(
    inputText: String,
    onInputChange: (String) -> Unit,
    attachedImageUri: String?,
    audioFile: java.io.File?,
    isRecording: Boolean,
    recordingSeconds: Int,
    isTyping: Boolean,
    onSend: () -> Unit,
    onPlusClick: () -> Unit,
    onRemoveAttachment: () -> Unit,
    onRemoveAudio: () -> Unit,
    onStopRecording: () -> Unit
) {
    Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
        Column(modifier = Modifier.navigationBarsPadding().padding(8.dp)) {

            if (attachedImageUri != null) {
                Box(modifier = Modifier.padding(bottom = 8.dp).size(80.dp)) {
                    AsyncImage(model = attachedImageUri, contentDescription = "Attachment",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp)))
                    IconButton(onClick = onRemoveAttachment,
                        modifier = Modifier.align(Alignment.TopEnd).size(24.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)) {
                        Icon(Icons.Default.Close, "Remove", tint = Color.White,
                            modifier = Modifier.size(14.dp))
                    }
                }
            }

            if (isRecording) {
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color.Red))
                        Text("Recording… ${recordingSeconds}s / 30s",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFFD32F2F), modifier = Modifier.weight(1f))
                        LinearProgressIndicator(progress = { recordingSeconds / 30f },
                            modifier = Modifier.width(80.dp),
                            color = Color(0xFFD32F2F), trackColor = Color(0xFFFFCDD2))
                        TextButton(onClick = onStopRecording) {
                            Text("Stop", color = Color(0xFFD32F2F))
                        }
                    }
                }
            }

            if (audioFile != null && !isRecording) {
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.GraphicEq, null, tint = Color(0xFF1976D2),
                            modifier = Modifier.size(20.dp))
                        Text("🎤 Voice message ready",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF1565C0), modifier = Modifier.weight(1f))
                        IconButton(onClick = onRemoveAudio, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, "Remove audio",
                                tint = Color(0xFF1976D2), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onPlusClick, enabled = !isTyping && !isRecording) {
                    Icon(Icons.Default.Add, "Attach",
                        tint = if (isTyping || isRecording)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        else MaterialTheme.colorScheme.primary)
                }
                OutlinedTextField(
                    value = inputText, onValueChange = onInputChange,
                    placeholder = { Text(stringResource(R.string.chat_input_hint)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 4, enabled = !isTyping,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                )
                val canSend = (inputText.isNotBlank() || attachedImageUri != null
                        || audioFile != null) && !isTyping && !isRecording
                IconButton(
                    onClick = onSend, enabled = canSend,
                    modifier = Modifier.size(48.dp).background(
                        color = if (canSend) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                        shape = CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, "Send",
                        tint = if (canSend) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                }
            }
        }
    }
}