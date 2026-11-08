package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.model.AppLanguage
import com.example.ui.components.StarkHudCard
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.StarkBackground
import com.example.ui.theme.StarkCyan
import com.example.ui.theme.StarkGold
import com.example.ui.theme.StarkSuccess
import com.example.ui.theme.StarkSurface
import com.example.ui.theme.StarkSurfaceElevated
import com.example.viewmodel.VisionAiViewModel

@Composable
fun SettingsScreen(
    viewModel: VisionAiViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val language by viewModel.language.collectAsState()
    val ttsPitch by viewModel.ttsPitch.collectAsState()
    val ttsSpeed by viewModel.ttsSpeed.collectAsState()
    val isMuted by viewModel.isMuted.collectAsState()

    var micPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var cameraPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        micPermissionGranted = isGranted
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        cameraPermissionGranted = isGranted
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StarkBackground)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(StarkGold)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SYSTEM CONTROLS & PERMISSIONS",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricBlue,
                letterSpacing = 1.2.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Language Selection Card
        StarkHudCard(
            title = "Communication Dialect (भाषा चयन)",
            subBadge = language.label
        ) {
            Column {
                Text(
                    text = "Select preferred natural language for speech recognition and JARVIS responses:",
                    fontFamily = FontFamily.Default,
                    fontSize = 12.sp,
                    color = Color(0xFFC3D9F2)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppLanguage.entries.forEach { lang ->
                        val isSelected = language == lang
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(0xFF003854) else Color(0xFF0A1830))
                                .border(
                                    1.dp,
                                    if (isSelected) ElectricBlue else Color(0xFF1B3250),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.setLanguage(lang) }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = lang.label,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) StarkCyan else Color(0xFF8BA6C7)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Speech & Audio Synthesizer Card
        StarkHudCard(
            title = "Audio Synthesizer Parameters",
            subBadge = if (isMuted) "MUTED" else "VOICE ACTIVE"
        ) {
            Column {
                // Mute toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "JARVIS Voice Feedback (TTS)",
                        fontFamily = FontFamily.Default,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                    Switch(
                        checked = !isMuted,
                        onCheckedChange = { viewModel.toggleMute() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ElectricBlue,
                            checkedTrackColor = Color(0xFF003D5C),
                            uncheckedThumbColor = Color(0xFF6B8CAD),
                            uncheckedTrackColor = Color(0xFF0C192E)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Pitch slider
                Text(
                    text = "VOICE PITCH: ${String.format("%.1f", ttsPitch)}x",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = StarkCyan
                )
                Slider(
                    value = ttsPitch,
                    onValueChange = { viewModel.setPitch(it) },
                    valueRange = 0.5f..1.8f,
                    colors = SliderDefaults.colors(
                        thumbColor = ElectricBlue,
                        activeTrackColor = ElectricBlue,
                        inactiveTrackColor = Color(0xFF183354)
                    )
                )

                // Speed slider
                Text(
                    text = "SPEECH RATE: ${String.format("%.1f", ttsSpeed)}x",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = StarkCyan
                )
                Slider(
                    value = ttsSpeed,
                    onValueChange = { viewModel.setSpeed(it) },
                    valueRange = 0.6f..1.6f,
                    colors = SliderDefaults.colors(
                        thumbColor = ElectricBlue,
                        activeTrackColor = ElectricBlue,
                        inactiveTrackColor = Color(0xFF183354)
                    )
                )

                // Test voice button
                Button(
                    onClick = {
                        val testMsg = if (language == AppLanguage.HINDI) {
                            "नमस्ते सर! स्टार्क ऑडियो सिंथेसाइज़र सक्रिय है।"
                        } else {
                            "Stark audio telemetry operational. All voice frequencies nominal, sir."
                        }
                        viewModel.speakText(testMsg)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF003854),
                        contentColor = StarkCyan
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.RecordVoiceOver,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TEST JARVIS VOICE OUTPUT",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Hardware Permissions Manager Card
        StarkHudCard(
            title = "Hardware & Protocol Permissions",
            subBadge = "SECURITY"
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Mic Permission
                PermissionRow(
                    name = "Microphone Access",
                    description = "Required for speech recognition and voice commands",
                    isGranted = micPermissionGranted,
                    onRequest = {
                        micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                )

                // Camera Permission
                PermissionRow(
                    name = "Camera Sensor",
                    description = "Required for optical live capture",
                    isGranted = cameraPermissionGranted,
                    onRequest = {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                )

                // System Settings Link
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF8BA6C7)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "OPEN ANDROID APP PERMISSION SETTINGS",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // AI Architecture & Backend Security Card
        StarkHudCard(
            title = "Secure Backend & AI Architecture",
            subBadge = "PROTECTED"
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = StarkSuccess,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Zero API Keys Exposed in Frontend",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StarkSuccess
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "• AI Logic Attestation: Designed for zero-trust token exchange via Firebase App Check.\n• Local Stark Core: Currently running full on-device Stark engine for instant voice recognition, bilingual dialogue, app launching, WhatsApp, and phone calling.\n• Cloud Gemini API: To switch from local Stark core to live multi-turn cloud Gemini, enable Firebase through the AI Studio setup prompt.",
                    fontFamily = FontFamily.Default,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    color = Color(0xFF9ABBDC)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun PermissionRow(
    name: String,
    description: String,
    isGranted: Boolean,
    onRequest: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF081428))
            .border(0.8.dp, Color(0xFF142E4C), RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isGranted) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (isGranted) StarkSuccess else StarkGold,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = name,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Text(
                text = description,
                fontFamily = FontFamily.Default,
                fontSize = 10.sp,
                color = Color(0xFF8BA6C7),
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        if (isGranted) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF003822))
                    .border(0.8.dp, StarkSuccess, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "ACTIVE",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = StarkSuccess
                )
            }
        } else {
            Button(
                onClick = onRequest,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricBlue,
                    contentColor = StarkBackground
                ),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = "GRANT",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
