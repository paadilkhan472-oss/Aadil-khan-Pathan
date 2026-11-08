package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.model.AppLanguage
import com.example.ui.components.StarkHudCard
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.StarkArcCore
import com.example.ui.theme.StarkBackground
import com.example.ui.theme.StarkCyan
import com.example.ui.theme.StarkGold
import com.example.ui.theme.StarkSuccess
import com.example.ui.theme.StarkSurface
import com.example.ui.theme.StarkSurfaceElevated
import com.example.viewmodel.VisionAiViewModel

@Composable
fun VisionScreen(
    viewModel: VisionAiViewModel,
    modifier: Modifier = Modifier
) {
    val selectedImageUri by viewModel.selectedImageUri.collectAsState()
    val isAnalyzing by viewModel.isAnalyzingImage.collectAsState()
    val scanResult by viewModel.scanResult.collectAsState()
    val language by viewModel.language.collectAsState()

    val scrollState = rememberScrollState()

    // Photo picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        viewModel.onImageSelected(uri)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StarkBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Optical HUD Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(ElectricBlue)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "OPTICAL HUD • MULTIMODAL SCANNER",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricBlue,
                letterSpacing = 1.2.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Viewport Frame / Image Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF071120))
                .border(1.5.dp, if (isAnalyzing) StarkGold else ElectricBlue, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Target optical feed",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Tactical Reticle Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .border(1.dp, StarkCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                )

                // Close / Clear Button
                IconButton(
                    onClick = { viewModel.clearSelectedImage() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0x99000000))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear Image",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                if (isAnalyzing) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0x99050C1B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = ElectricBlue,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "RUNNING SPECTRAL SCAN...",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = StarkCyan,
                                fontSize = 12.sp,
                                letterSpacing = 1.5.sp
                            )
                        }
                    }
                }
            } else {
                // Empty State Prompt
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = ElectricBlue.copy(alpha = 0.6f),
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "NO OPTICAL FEED LOADED",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Select a photo or document to run JARVIS AI visual understanding",
                        fontFamily = FontFamily.Default,
                        fontSize = 11.sp,
                        color = Color(0xFF8BA6C7)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons to Load Photo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricBlue,
                    contentColor = StarkBackground
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("upload_image_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "SELECT PHOTO",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }

            if (selectedImageUri != null) {
                OutlinedButton(
                    onClick = {
                        selectedImageUri?.let { viewModel.scanImage(it) }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = StarkCyan
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("rescan_image_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "RE-SCAN",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Analysis Results Card
        AnimatedVisibility(visible = scanResult != null) {
            scanResult?.let { res ->
                StarkHudCard(
                    title = "Tactical Optical Assessment",
                    subBadge = "CONFIDENCE ${res.confidenceScore}%"
                ) {
                    Column {
                        // Title & Read Aloud
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = res.title,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            IconButton(
                                onClick = {
                                    val speech = if (language == AppLanguage.HINDI) res.hindiSummary else res.summary
                                    viewModel.speakText(speech)
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Speak Report",
                                    tint = ElectricBlue
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // English Summary
                        Text(
                            text = res.summary,
                            fontFamily = FontFamily.Default,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = Color(0xFFD4E5F9)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Hindi Summary Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF09162D))
                                .border(0.8.dp, Color(0xFF16325C), RoundedCornerShape(6.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = "हिन्दी सारांश (HINDI SUMMARY):",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StarkGold
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = res.hindiSummary,
                                    fontFamily = FontFamily.Default,
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Detected Objects Chips
                        Text(
                            text = "DETECTED OBJECTS & CONTOURS:",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Color(0xFF8BA6C7)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            res.detectedObjects.forEach { obj ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF003852))
                                        .border(0.8.dp, StarkCyan.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = obj,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = StarkCyan
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Tactical Assessment
                        Text(
                            text = "TACTICAL THREAT EVALUATION:",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Color(0xFF8BA6C7)
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = StarkSuccess,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = res.tacticalAssessment,
                                fontFamily = FontFamily.Default,
                                fontSize = 12.sp,
                                color = StarkSuccess
                            )
                        }
                    }
                }
            }
        }
    }
}
