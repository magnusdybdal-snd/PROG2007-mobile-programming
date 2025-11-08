package com.example.motorcycleparking.features.settingsscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.motorcycleparking.core.core_ui.composable.SettingToggleItem
import com.example.motorcycleparking.core.core_ui.composable.SettingsSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel
) {
    val settingsState by viewModel.settingsState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Map Settings Section
            SettingsSection(title = "Map Settings") {
                SettingToggleItem(
                    icon = Icons.Default.Traffic,
                    title = "Show Traffic",
                    description = "Display real-time traffic information",
                    checked = settingsState.showTraffic,
                    onCheckedChange = { viewModel.updateShowTraffic(it) }
                )

                SettingToggleItem(
                    icon = Icons.Default.MyLocation,
                    title = "My Location Button",
                    description = "Show button to center map on your location",
                    checked = settingsState.showMyLocationButton,
                    onCheckedChange = { viewModel.updateMyLocationButton(it) }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = DividerDefaults.Thickness,
                    color = DividerDefaults.color
                )

                // Location Settings Section
                SettingsSection(title = "Location Settings") {
                    SettingToggleItem(
                        icon = Icons.Default.GpsFixed,
                        title = "Auto-Center on Location",
                        description = "Automatically center map on your location",
                        checked = settingsState.autoCenterOnLocation,
                        onCheckedChange = { viewModel.updateAutoCenterOnLocation(it) }
                    )

                    SettingToggleItem(
                        icon = Icons.Default.Update,
                        title = "Continuous Location Updates",
                        description = "Keep updating your location in real-time",
                        checked = settingsState.continuousLocationUpdates,
                        onCheckedChange = { viewModel.updateContinuousLocationUpdates(it) }
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = DividerDefaults.Thickness,
                    color = DividerDefaults.color
                )

                // Parking Settings Section
                SettingToggleItem(
                    icon = Icons.Default.Star,
                    title = "Show Favorites Only",
                    description = "Display only your saved favorite spots",
                    checked = settingsState.showFavoritesOnly,
                    onCheckedChange = { viewModel.updateShowFavoritesOnly(it) }
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = DividerDefaults.Thickness,
                color = DividerDefaults.color
            )

            // General Settings Section
            SettingsSection(title = "General Settings") {
                SettingToggleItem(
                    icon = Icons.Default.DarkMode,
                    title = "Dark Mode",
                    description = "Use dark theme",
                    checked = settingsState.darkMode,
                    onCheckedChange = { viewModel.updateDarkMode(it) }
                )

            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}