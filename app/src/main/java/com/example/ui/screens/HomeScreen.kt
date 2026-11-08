package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.QuickCommand
import com.example.ui.components.ArcReactorMicButton
import com.example.ui.components.StarkHudCard
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.StarkArcCore
import com.example.ui.theme.StarkBackground
import com.example.ui.theme.StarkCyan
import com.example.ui.theme.StarkGold
import com.example.ui.theme.StarkSuccess
import com.example.ui.theme.StarkSurfaceElevated
import com.example.viewmodel.MainNavigationTab
import com.example.viewmodel.VisionAiViewModel

@Composable
fun HomeScreen(
    viewModel: VisionAiViewModel,
    modifier: Modifier = Modifier
) {
    val telemetry by viewModel.telemetry.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val amplitude by viewModel.rmsAmplitude.collectAsState()
    val recognizedText by viewModel.recognizedText.collectAsState()
    val language by viewModel.language.collectAsState()
    val speechError by viewModel.speechError.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StarkBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header: Stark Systems Status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(StarkSuccess)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "MARK L • JARVIS ACTIVE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricBlue,
                        letterSpacing = 1.2.sp
                    )
                }
                Text(
                    text = "VISION AI",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
            }

            // Language Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(StarkSurfaceElevated)
                    .border(1.dp, ElectricBlue.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .clickable {
                        val nextLang = when (language) {
                            AppLanguage.AUTO -> AppLanguage.ENGLISH
                            AppLanguage.ENGLISH -> AppLanguage.HINDI
                            AppLanguage.HINDI -> AppLanguage.AUTO
                        }
                        viewModel.setLanguage(nextLang)
                    }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("home_language_toggle")
            ) {
                Text(
                    text = "LANG: ${language.label}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = StarkCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Telemetry Mini-Meters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TelemetryBadge(
                label = "ARC POWER",
                value = "${telemetry.arcReactorPower}%",
                accentColor = ElectricBlue,
                modifier = Modifier.weight(1f)
            )
            TelemetryBadge(
                label = "CPU LOAD",
                value = "${telemetry.cpuLoad}%",
                accentColor = StarkCyan,
                modifier = Modifier.weight(1f)
            )
            TelemetryBadge(
                label = "LINK STATUS",
                value = "OPTIMAL",
                accentColor = StarkGold,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Center Stage: Large TALK TO VISION Arc Reactor Mic
        ArcReactorMicButton(
            isListening = isListening,
            isSpeaking = isSpeaking,
            amplitude = amplitude,
            onClick = {
                viewModel.toggleSpeechListening()
            }
        )

        // Live Recognition Bubble / Speech Feedback
        AnimatedVisibility(visible = recognizedText.isNotBlank() || isListening) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF09142A))
                    .border(1.dp, ElectricBlue.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "VOICE INPUT STREAM:",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = StarkCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (recognizedText.isNotBlank()) "\"$recognizedText\"" else "Listening for English or Hindi instructions...",
                        fontFamily = FontFamily.Default,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }
        }

        // Speech Error notice if any
        if (speechError != null) {
            Text(
                text = speechError ?: "",
                color = Color(0xFFFF5252),
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Tactical Directives (Chips)
        Text(
            text = "DIRECT COMMANDS (इंग्लिश / हिन्दी)",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8BA6C7),
            letterSpacing = 1.2.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(viewModel.quickCommands) { cmd ->
                QuickCommandChip(
                    command = cmd,
                    isHindi = language == AppLanguage.HINDI,
                    onClick = {
                        val prompt = if (language == AppLanguage.HINDI) cmd.promptHi else cmd.promptEn
                        viewModel.sendUserMessage(prompt)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Subsystem Shortcuts
        StarkHudCard(
            title = "Subsystem Hub",
            subBadge = "READY"
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                QuickHubAction(
                    icon = Icons.Default.Chat,
                    label = "AI Terminal",
                    onClick = { viewModel.setTab(MainNavigationTab.CHAT) }
                )
                QuickHubAction(
                    icon = Icons.Default.CameraAlt,
                    label = "Vision Scanner",
                    onClick = { viewModel.setTab(MainNavigationTab.VISION) }
                )
                QuickHubAction(
                    icon = Icons.Default.PlayArrow,
                    label = "Apps Hub",
                    onClick = { viewModel.setTab(MainNavigationTab.APPS) }
                )
                QuickHubAction(
                    icon = Icons.Default.VolumeUp,
                    label = if (viewModel.isMuted.collectAsState().value) "Unmute" else "Mute TTS",
                    onClick = { viewModel.toggleMute() }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stark Industries Watermark Footer
        Text(
            text = "STARK INDUSTRIES AI LABS • QUANTUM MK-50 PROTOCOL",
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            color = Color(0xFF4A6B8A),
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun TelemetryBadge(
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF09142A))
            .border(1.dp, Color(0xFF132B4A), RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp, horizontal = 10.dp)
    ) {
        Column {
            Text(
                text = label,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                color = Color(0xFF8BA6C7),
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
        }
    }
}

@Composable
fun QuickCommandChip(
    command: QuickCommand,
    isHindi: Boolean,
    onClick: () -> Unit
) {
    val icon = when (command.iconName) {
        "speed" -> Icons.Default.Speed
        "chat" -> Icons.Default.Chat
        "phone" -> Icons.Default.Phone
        "play" -> Icons.Default.PlayArrow
        else -> Icons.Default.CameraAlt
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF0C1934),
                        Color(0xFF0E2248)
                    )
                )
            )
            .border(1.dp, ElectricBlue.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("quick_command_${command.id}")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = StarkCyan,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isHindi) command.titleHi else command.titleEn,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

@Composable
fun QuickHubAction(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFF0C1B38))
                .border(1.dp, ElectricBlue.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = StarkArcCore,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            color = Color(0xFFC7DEFF)
        )
    }
}
