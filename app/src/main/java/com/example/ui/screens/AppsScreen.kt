package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LaunchableApp
import com.example.ui.components.StarkHudCard
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.StarkBackground
import com.example.ui.theme.StarkCyan
import com.example.ui.theme.StarkGold
import com.example.ui.theme.StarkSurface
import com.example.viewmodel.VisionAiViewModel

@Composable
fun AppsScreen(
    viewModel: VisionAiViewModel,
    modifier: Modifier = Modifier
) {
    val installedApps by viewModel.installedApps.collectAsState()
    val searchQuery by viewModel.appSearchQuery.collectAsState()

    val filteredApps = if (searchQuery.isBlank()) {
        installedApps
    } else {
        installedApps.filter {
            it.appName.contains(searchQuery, ignoreCase = true) ||
                    it.packageName.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StarkBackground)
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
                    .background(StarkCyan)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "APPLICATION LAUNCHER MATRIX",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricBlue,
                letterSpacing = 1.2.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setAppSearchQuery(it) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = ElectricBlue
                )
            },
            placeholder = {
                Text(
                    text = "Filter applications by name...",
                    fontFamily = FontFamily.Default,
                    fontSize = 13.sp,
                    color = Color(0xFF6B8CAD)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("app_search_field"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricBlue,
                unfocusedBorderColor = Color(0xFF1B3860),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = StarkCyan
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Popular shortcuts card if no search
        if (searchQuery.isBlank()) {
            val popularApps = installedApps.filter { it.isPopular }.take(6)
            if (popularApps.isNotEmpty()) {
                StarkHudCard(
                    title = "Priority Access Apps",
                    subBadge = "${popularApps.size} READY"
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        popularApps.forEach { app ->
                            AppListItem(
                                app = app,
                                onLaunch = { viewModel.launchApp(app) }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        Text(
            text = "ALL DETECTED APPS (${filteredApps.size})",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8BA6C7),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // List of Apps
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filteredApps, key = { it.packageName }) { app ->
                AppListItem(
                    app = app,
                    onLaunch = { viewModel.launchApp(app) }
                )
            }
        }
    }
}

@Composable
fun AppListItem(
    app: LaunchableApp,
    onLaunch: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(StarkSurface)
            .border(1.dp, Color(0xFF132A4A), RoundedCornerShape(8.dp))
            .clickable(onClick = onLaunch)
            .padding(12.dp)
            .testTag("app_item_${app.packageName}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF0C1B34))
                        .border(1.dp, ElectricBlue.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Apps,
                        contentDescription = null,
                        tint = if (app.isPopular) StarkGold else StarkCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = app.appName,
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Text(
                        text = app.packageName,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = Color(0xFF6B8CAD)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF003852))
                    .border(0.8.dp, ElectricBlue.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Launch,
                        contentDescription = "Launch",
                        tint = StarkCyan,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "LAUNCH",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = StarkCyan
                    )
                }
            }
        }
    }
}
