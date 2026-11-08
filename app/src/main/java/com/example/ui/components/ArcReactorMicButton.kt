package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.StarkArcCore
import com.example.ui.theme.StarkBackground
import com.example.ui.theme.StarkCyan
import com.example.ui.theme.StarkGold
import com.example.ui.theme.StarkTextPrimary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ArcReactorMicButton(
    isListening: Boolean,
    isSpeaking: Boolean,
    amplitude: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "reactor_anim")

    // Outer ring slow rotation
    val rotationOuter by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation_outer"
    )

    // Inner ring counter-rotation
    val rotationInner by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation_inner"
    )

    // Core pulsing glow
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isListening) 1.25f else 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isListening) 600 else 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val dynamicScale = if (isListening) pulseScale + (amplitude * 0.2f) else pulseScale

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(170.dp)
                .testTag("talk_to_vision_button")
        ) {
            // Arc Reactor Canvas (Rings, Ticks, Glowing Circles)
            Canvas(
                modifier = Modifier
                    .size(170.dp)
                    .scale(dynamicScale)
            ) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val maxRadius = size.width / 2f

                // Outer ambient glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            ElectricBlue.copy(alpha = if (isListening) 0.35f else 0.12f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = maxRadius
                    ),
                    radius = maxRadius,
                    center = center
                )

                // Outer Tech Ring (Dashed)
                drawCircle(
                    color = ElectricBlue.copy(alpha = 0.6f),
                    radius = maxRadius - 10.dp.toPx(),
                    center = center,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 10f), rotationOuter)
                    )
                )

                // Secondary Rotating Accent Ring
                drawCircle(
                    color = StarkCyan.copy(alpha = 0.5f),
                    radius = maxRadius - 22.dp.toPx(),
                    center = center,
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(25f, 16f), rotationInner)
                    )
                )

                // 12 Stark Peripheral Segment Ticks
                val tickRadius = maxRadius - 32.dp.toPx()
                for (i in 0 until 12) {
                    val angle = Math.toRadians((i * 30 + rotationOuter).toDouble())
                    val startX = center.x + (tickRadius - 8.dp.toPx()) * cos(angle).toFloat()
                    val startY = center.y + (tickRadius - 8.dp.toPx()) * sin(angle).toFloat()
                    val endX = center.x + tickRadius * cos(angle).toFloat()
                    val endY = center.y + tickRadius * sin(angle).toFloat()

                    drawLine(
                        color = if (i % 3 == 0) StarkGold else ElectricBlue,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = if (i % 3 == 0) 3.dp.toPx() else 1.5.dp.toPx()
                    )
                }

                // Inner Arc Reactor Solid Glow Ring
                drawCircle(
                    color = ElectricBlue.copy(alpha = if (isListening) 0.8f else 0.4f),
                    radius = maxRadius - 44.dp.toPx(),
                    center = center,
                    style = Stroke(width = 3.dp.toPx())
                )
            }

            // Interactive Center Core Button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(86.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = if (isListening) {
                                listOf(StarkArcCore, ElectricBlue, StarkBackground)
                            } else {
                                listOf(ElectricBlue.copy(alpha = 0.85f), Color(0xFF061838), StarkBackground)
                            }
                        )
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, color = StarkCyan),
                        onClick = onClick
                    )
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = if (isListening) "Stop Listening" else "Talk to Vision AI",
                    tint = if (isListening) StarkBackground else StarkTextPrimary,
                    modifier = Modifier.size(38.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Futuristic Status Label
        Text(
            text = when {
                isListening -> "● LISTENING TO DIRECTIVE..."
                isSpeaking -> "⚡ JARVIS TRANSMITTING..."
                else -> "TALK TO VISION"
            },
            color = if (isListening) StarkCyan else ElectricBlue,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            letterSpacing = 1.6.sp
        )

        Text(
            text = if (isListening) "Tap core to finalize speech" else "Tap Arc Reactor to activate voice uplink",
            color = Color(0xFF8BA6C7),
            fontSize = 11.sp,
            fontFamily = FontFamily.Default,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
