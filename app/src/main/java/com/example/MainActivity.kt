package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ActionConfirmationDialog
import com.example.ui.screens.AppsScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VisionScreen
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.StarkBackground
import com.example.ui.theme.StarkCyan
import com.example.ui.theme.StarkSurface
import com.example.ui.theme.VisionAITheme
import com.example.viewmodel.MainNavigationTab
import com.example.viewmodel.VisionAiViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: VisionAiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            VisionAITheme {
                MainVisionApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainVisionApp(viewModel: VisionAiViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val pendingAction by viewModel.pendingAction.collectAsState()

    // Handle back button on sub-screens to return to Home
    if (currentTab != MainNavigationTab.HOME) {
        BackHandler {
            viewModel.setTab(MainNavigationTab.HOME)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = StarkSurface,
                modifier = Modifier
                    .border(0.5.dp, Color(0xFF152A4A))
                    .height(68.dp)
            ) {
                val navItems = listOf(
                    NavigationItem(MainNavigationTab.HOME, "Home", Icons.Default.Home, "nav_home"),
                    NavigationItem(MainNavigationTab.CHAT, "Chat", Icons.Default.Chat, "nav_chat"),
                    NavigationItem(MainNavigationTab.VISION, "Vision", Icons.Default.CameraAlt, "nav_vision"),
                    NavigationItem(MainNavigationTab.APPS, "Apps", Icons.Default.Apps, "nav_apps"),
                    NavigationItem(MainNavigationTab.SETTINGS, "Settings", Icons.Default.Settings, "nav_settings")
                )

                navItems.forEach { item ->
                    val isSelected = currentTab == item.tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setTab(item.tab) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = StarkBackground,
                            selectedTextColor = StarkCyan,
                            unselectedIconColor = Color(0xFF7593B4),
                            unselectedTextColor = Color(0xFF7593B4),
                            indicatorColor = ElectricBlue
                        ),
                        modifier = Modifier.testTag(item.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(StarkBackground)
                .padding(innerPadding)
        ) {
            when (currentTab) {
                MainNavigationTab.HOME -> HomeScreen(viewModel = viewModel)
                MainNavigationTab.CHAT -> ChatScreen(viewModel = viewModel)
                MainNavigationTab.VISION -> VisionScreen(viewModel = viewModel)
                MainNavigationTab.APPS -> AppsScreen(viewModel = viewModel)
                MainNavigationTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
            }

            // Global Action Confirmation Dialog
            pendingAction?.let { action ->
                ActionConfirmationDialog(
                    action = action,
                    onConfirm = { viewModel.confirmPendingAction() },
                    onDismiss = { viewModel.dismissPendingAction() }
                )
            }
        }
    }
}

data class NavigationItem(
    val tab: MainNavigationTab,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val tag: String
)
