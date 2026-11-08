package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.StarkBorder
import com.example.ui.theme.StarkCyan
import com.example.ui.theme.StarkSurface
import com.example.ui.theme.StarkSurfaceElevated

@Composable
fun StarkHudCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    subBadge: String? = null,
    borderColor: Color = StarkBorder,
    glowColor: Color = ElectricBlue.copy(alpha = 0.2f),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        StarkSurfaceElevated.copy(alpha = 0.85f),
                        StarkSurface.copy(alpha = 0.95f)
                    )
                )
            )
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Column {
            if (title != null || subBadge != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    // Futuristic HUD Tech Pip
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(ElectricBlue, RoundedCornerShape(1.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    if (title != null) {
                        Text(
                            text = title.uppercase(),
                            color = StarkCyan,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.4.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (subBadge != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(glowColor)
                                .border(1.dp, borderColor.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = subBadge,
                                color = ElectricBlue,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            content()
        }
    }
}
