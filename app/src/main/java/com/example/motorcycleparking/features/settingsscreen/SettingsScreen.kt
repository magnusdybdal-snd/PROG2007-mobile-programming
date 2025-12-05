package com.example.motorcycleparking.features.settingsscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.motorcycleparking.core.core_ui.composable.PageHeading
import com.example.motorcycleparking.core.core_ui.composable.settings.SettingToggleItem
import com.example.motorcycleparking.core.core_ui.composable.settings.SettingsDivider
import com.example.motorcycleparking.core.core_ui.composable.settings.SettingsSection
import com.example.motorcycleparking.data.api.FirebaseRepo
import timber.log.Timber

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: SettingsViewModel,
) {
    val settingsState by viewModel.settingsState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        PageHeading(
            modifier = Modifier,
            text = "Settings"
        )
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


                SettingsDivider() // visible divider-line


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

                SettingsDivider() // visible divider-line

                // Parking Settings Section
                SettingsSection(title = "Favorites Settings") {
                    SettingToggleItem(
                        icon = Icons.Default.Star,
                        title = "Show Favorites Only",
                        description = "Display only your saved favorite spots",
                        checked = settingsState.showFavoritesOnly,
                        onCheckedChange = { viewModel.updateShowFavoritesOnly(it) }
                    )
                }
            }

            SettingsDivider() // visible divider-line

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

            SettingsDivider() // visible divider-line

            SettingsSection(title = "Account Settings") {
                Button( // Sign out button
                    onClick ={
                        viewModel.signOut() // sign user out
                        onNavigateToLogin() // Navigate to login screen
                             },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sign Out",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
    }
}