package com.example.motorcycleparking.features.settingsscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DataSaverOn
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
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
                    icon = Icons.Default.LocationCity,
                    title = "3D Buildings",
                    description = "Show 3D building structures",
                    checked = settingsState.show3DBuildings,
                    onCheckedChange = { viewModel.update3DBuildings(it) }
                )

                SettingToggleItem(
                    icon = Icons.Default.MyLocation,
                    title = "My Location Button",
                    description = "Show button to center map on your location",
                    checked = settingsState.showMyLocationButton,
                    onCheckedChange = { viewModel.updateMyLocationButton(it) }
                )

                SettingToggleItem(
                    icon = Icons.Default.ZoomIn,
                    title = "Zoom Controls",
                    description = "Display zoom in/out buttons",
                    checked = settingsState.showZoomControls,
                    onCheckedChange = { viewModel.updateZoomControls(it) }
                )
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

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

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Parking Settings Section
            SettingsSection(title = "Parking Settings") {
                SettingToggleItem(
                    icon = Icons.Default.Notifications,
                    title = "Parking Notifications",
                    description = "Get notified about nearby parking spots",
                    checked = settingsState.parkingNotifications,
                    onCheckedChange = { viewModel.updateParkingNotifications(it) }
                )

                SettingToggleItem(
                    icon = Icons.Default.Star,
                    title = "Show Favorites Only",
                    description = "Display only your saved favorite spots",
                    checked = settingsState.showFavoritesOnly,
                    onCheckedChange = { viewModel.updateShowFavoritesOnly(it) }
                )

                SettingToggleItem(
                    icon = Icons.Default.FilterAlt,
                    title = "Auto-Filter Available Spots",
                    description = "Only show available parking spots",
                    checked = settingsState.autoFilterAvailable,
                    onCheckedChange = { viewModel.updateAutoFilterAvailable(it) }
                )
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // General Settings Section
            SettingsSection(title = "General Settings") {
                SettingToggleItem(
                    icon = Icons.Default.DarkMode,
                    title = "Dark Mode",
                    description = "Use dark theme",
                    checked = settingsState.darkMode,
                    onCheckedChange = { viewModel.updateDarkMode(it) }
                )

                SettingToggleItem(
                    icon = Icons.Default.Vibration,
                    title = "Haptic Feedback",
                    description = "Vibrate on interactions",
                    checked = settingsState.hapticFeedback,
                    onCheckedChange = { viewModel.updateHapticFeedback(it) }
                )

                SettingToggleItem(
                    icon = Icons.Default.DataSaverOn,
                    title = "Data Saver Mode",
                    description = "Reduce data usage",
                    checked = settingsState.dataSaverMode,
                    onCheckedChange = { viewModel.updateDataSaverMode(it) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        content()
    }
}

@Composable
fun SettingToggleItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}
