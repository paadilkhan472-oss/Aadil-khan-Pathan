package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.ChatMessage
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.StarkArcCore
import com.example.ui.theme.StarkBackground
import com.example.ui.theme.StarkCyan
import com.example.ui.theme.StarkGold
import com.example.ui.theme.StarkSurface
import com.example.ui.theme.StarkSurfaceElevated
import com.example.viewmodel.VisionAiViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(
    viewModel: VisionAiViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.messages.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val recognizedText by viewModel.recognizedText.collectAsState()
    var inputText by remember { mutableStateOf("") }
    var attachedImageUri by remember { mutableStateOf<Uri?>(null) }

    val listState = rememberLazyListState()

    // Sync recognized speech into input text or trigger directly
    LaunchedEffect(recognizedText) {
        if (recognizedText.isNotBlank()) {
            inputText = recognizedText
        }
    }

    // Scroll to bottom on new message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        attachedImageUri = uri
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StarkBackground)
            .imePadding()
    ) {
        // Chat Terminal Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(StarkSurface)
                .border(0.dp, Color.Transparent)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0C1D3F))
                            .border(1.dp, ElectricBlue, CircleShape)
                    ) {
                        Text(
                            text = "J",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = StarkCyan,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "JARVIS AI TERMINAL",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "BILINGUAL COGNITIVE STREAM (EN/HI)",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = ElectricBlue
                        )
                    }
                }

                // Status chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF00334E))
                        .border(1.dp, StarkCyan.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "ONLINE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = StarkCyan
                    )
                }
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                ChatMessageItem(
                    message = msg,
                    onSpeak = { viewModel.speakText(msg.text) },
                    onActionClick = { action -> viewModel.triggerActionConfirmation(action) }
                )
            }
        }

        // Image Attachment Preview bar (if selected)
        if (attachedImageUri != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0C1935))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                AsyncImage(
                    model = attachedImageUri,
                    contentDescription = "Attached image",
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .border(1.dp, ElectricBlue, RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Image attached for optical analysis",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = StarkArcCore,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { attachedImageUri = null }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove attachment",
                        tint = Color.White
                    )
                }
            }
        }

        // Bottom Input Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(StarkSurfaceElevated)
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            // Image Attachment Button
            IconButton(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier.testTag("chat_pick_image_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Attach Image",
                    tint = StarkCyan
                )
            }

            // Mic Toggle Button
            IconButton(
                onClick = {
                    viewModel.toggleSpeechListening()
                },
                modifier = Modifier.testTag("chat_mic_button")
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = "Voice Input",
                    tint = if (isListening) StarkGold else ElectricBlue
                )
            }

            // Text Input Field
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = {
                    Text(
                        text = if (isListening) "Listening to voice..." else "Command JARVIS in English or Hindi...",
                        fontFamily = FontFamily.Default,
                        fontSize = 13.sp,
                        color = Color(0xFF6B8CAD)
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricBlue,
                    unfocusedBorderColor = Color(0xFF1B3860),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = StarkCyan
                ),
                shape = RoundedCornerShape(24.dp),
                maxLines = 4
            )

            Spacer(modifier = Modifier.width(4.dp))

            // Send Button
            IconButton(
                onClick = {
                    if (inputText.isNotBlank() || attachedImageUri != null) {
                        viewModel.sendUserMessage(inputText, attachedImageUri)
                        inputText = ""
                        attachedImageUri = null
                    }
                },
                modifier = Modifier.testTag("chat_send_button")
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(ElectricBlue, Color(0xFF00557A))
                            )
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Command",
                        tint = StarkBackground,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    onSpeak: () -> Unit,
    onActionClick: (com.example.model.ActionConfirmation) -> Unit
) {
    val isUser = message.isUser
    val alignment = if (isUser) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        // Speaker label
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 3.dp, start = 4.dp, end = 4.dp)
        ) {
            Text(
                text = if (isUser) "OPERATOR" else "JARVIS [VISION AI]",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isUser) StarkGold else StarkCyan,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp)),
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                color = Color(0xFF5F7D9E)
            )
        }

        // Message Bubble
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 14.dp,
                        topEnd = 14.dp,
                        bottomStart = if (isUser) 14.dp else 2.dp,
                        bottomEnd = if (isUser) 2.dp else 14.dp
                    )
                )
                .background(
                    if (isUser) {
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF0B2B54), Color(0xFF071B35))
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF0C1E3C), Color(0xFF071224))
                        )
                    }
                )
                .border(
                    1.dp,
                    if (isUser) Color(0xFF1B5AA0) else ElectricBlue.copy(alpha = 0.5f),
                    RoundedCornerShape(
                        topStart = 14.dp,
                        topEnd = 14.dp,
                        bottomStart = if (isUser) 14.dp else 2.dp,
                        bottomEnd = if (isUser) 2.dp else 14.dp
                    )
                )
                .padding(12.dp)
        ) {
            Column {
                // Attached image if any
                if (message.imageUri != null) {
                    AsyncImage(
                        model = message.imageUri,
                        contentDescription = "User uploaded image",
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(160.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, ElectricBlue, RoundedCornerShape(8.dp))
                            .padding(bottom = 6.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                // Message text
                Text(
                    text = message.text,
                    fontFamily = FontFamily.Default,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = Color.White
                )

                // Action Confirmation Button Pill if action suggested
                if (message.suggestedAction != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF003852))
                            .border(1.dp, ElectricBlue, RoundedCornerShape(6.dp))
                            .clickable { onActionClick(message.suggestedAction) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "⚡ AUTHORIZE: ${message.suggestedAction.title}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = StarkCyan
                            )
                        }
                    }
                }

                // Source Note / TTS Read Aloud Icon
                if (!isUser) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (message.sourceNote != null) {
                            Text(
                                text = message.sourceNote,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                color = Color(0xFF6B8EA8),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        IconButton(
                            onClick = onSpeak,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Read Aloud",
                                tint = ElectricBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
