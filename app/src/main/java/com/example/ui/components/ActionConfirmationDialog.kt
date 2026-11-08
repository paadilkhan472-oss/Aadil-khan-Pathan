package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.ActionConfirmation
import com.example.model.ActionType
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.StarkBackground
import com.example.ui.theme.StarkCyan
import com.example.ui.theme.StarkError
import com.example.ui.theme.StarkGold
import com.example.ui.theme.StarkSuccess
import com.example.ui.theme.StarkSurface
import com.example.ui.theme.StarkSurfaceElevated

@Composable
fun ActionConfirmationDialog(
    action: ActionConfirmation,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val actionColor = when (action.type) {
        ActionType.WHATSAPP -> StarkSuccess
        ActionType.PHONE_CALL -> ElectricBlue
        ActionType.LAUNCH_APP -> StarkCyan
    }

    val actionIcon = when (action.type) {
        ActionType.WHATSAPP -> Icons.Default.Chat
        ActionType.PHONE_CALL -> Icons.Default.Call
        ActionType.LAUNCH_APP -> Icons.Default.Launch
    }

    val actionTypeLabel = when (action.type) {
        ActionType.WHATSAPP -> "WHATSAPP TRANSMISSION PROTOCOL"
        ActionType.PHONE_CALL -> "VOICE UPLINK PROTOCOL"
        ActionType.LAUNCH_APP -> "SYSTEM APPLICATION LAUNCH"
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            StarkSurfaceElevated,
                            StarkSurface,
                            StarkBackground
                        )
                    )
                )
                .border(BorderStroke(1.5.dp, actionColor), RoundedCornerShape(16.dp))
                .padding(20.dp)
                .testTag("action_confirmation_dialog")
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Security Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Security Alert",
                        tint = StarkGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "JARVIS PROTOCOL AUTHORIZATION",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StarkGold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Icon Orb
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(actionColor.copy(alpha = 0.15f))
                        .border(1.dp, actionColor, CircleShape)
                ) {
                    Icon(
                        imageVector = actionIcon,
                        contentDescription = action.title,
                        tint = actionColor,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = actionTypeLabel,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = actionColor,
                    letterSpacing = 1.2.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Details Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF070E1C))
                        .border(0.8.dp, Color(0xFF1B3150), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "TARGET:",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Color(0xFF8BA6C7)
                        )
                        Text(
                            text = action.target,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "PAYLOAD / DETAILS:",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = Color(0xFF8BA6C7)
                        )
                        Text(
                            text = action.details,
                            fontFamily = FontFamily.Default,
                            fontSize = 13.sp,
                            color = Color(0xFFD4E5F9)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Protocol requires conscious operator sign-off before dispatching to external system.",
                    fontFamily = FontFamily.Default,
                    fontSize = 11.sp,
                    color = Color(0xFF8BA6C7),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        border = BorderStroke(1.dp, StarkError.copy(alpha = 0.8f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = StarkError
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("action_abort_button")
                    ) {
                        Text(
                            text = "ABORT",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = actionColor,
                            contentColor = StarkBackground
                        ),
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("action_confirm_button")
                    ) {
                        Text(
                            text = "AUTHORIZE",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
