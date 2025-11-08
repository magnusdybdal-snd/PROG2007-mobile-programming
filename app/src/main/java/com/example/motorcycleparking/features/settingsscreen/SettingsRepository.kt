package com.example.motorcycleparking.features.settingsscreen

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property to create DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    // Define keys for each setting
    private object PreferencesKeys {
        // Map Settings
        val SHOW_TRAFFIC = booleanPreferencesKey("show_traffic")
        val SHOW_3D_BUILDINGS = booleanPreferencesKey("show_3d_buildings")
        val SHOW_MY_LOCATION_BUTTON = booleanPreferencesKey("show_my_location_button")
        val SHOW_ZOOM_CONTROLS = booleanPreferencesKey("show_zoom_controls")

        // Location Settings
        val AUTO_CENTER_ON_LOCATION = booleanPreferencesKey("auto_center_on_location")
        val CONTINUOUS_LOCATION_UPDATES = booleanPreferencesKey("continuous_location_updates")

        // Parking Settings
        val PARKING_NOTIFICATIONS = booleanPreferencesKey("parking_notifications")
        val SHOW_FAVORITES_ONLY = booleanPreferencesKey("show_favorites_only")
        val AUTO_FILTER_AVAILABLE = booleanPreferencesKey("auto_filter_available")

        // General Settings
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val DATA_SAVER_MODE = booleanPreferencesKey("data_saver_mode")
    }

    // Flow to observe settings changes
    val settingsFlow: Flow<SettingsState> = context.dataStore.data.map { preferences ->
        SettingsState(
            // Map Settings
            showTraffic = preferences[PreferencesKeys.SHOW_TRAFFIC] ?: true,
            show3DBuildings = preferences[PreferencesKeys.SHOW_3D_BUILDINGS] ?: true,
            showMyLocationButton = preferences[PreferencesKeys.SHOW_MY_LOCATION_BUTTON] ?: true,
            showZoomControls = preferences[PreferencesKeys.SHOW_ZOOM_CONTROLS] ?: true,

            // Location Settings
            autoCenterOnLocation = preferences[PreferencesKeys.AUTO_CENTER_ON_LOCATION] ?: true,
            continuousLocationUpdates = preferences[PreferencesKeys.CONTINUOUS_LOCATION_UPDATES] ?: true,

            // Parking Settings
            parkingNotifications = preferences[PreferencesKeys.PARKING_NOTIFICATIONS] ?: true,
            showFavoritesOnly = preferences[PreferencesKeys.SHOW_FAVORITES_ONLY] ?: false,
            autoFilterAvailable = preferences[PreferencesKeys.AUTO_FILTER_AVAILABLE] ?: true,

            // General Settings
            darkMode = preferences[PreferencesKeys.DARK_MODE] ?: false,
            hapticFeedback = preferences[PreferencesKeys.HAPTIC_FEEDBACK] ?: true,
            dataSaverMode = preferences[PreferencesKeys.DATA_SAVER_MODE] ?: false
        )
    }

    // Map Settings
    suspend fun updateShowTraffic(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_TRAFFIC] = enabled
        }
    }

    suspend fun update3DBuildings(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_3D_BUILDINGS] = enabled
        }
    }

    suspend fun updateMyLocationButton(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_MY_LOCATION_BUTTON] = enabled
        }
    }

    suspend fun updateZoomControls(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_ZOOM_CONTROLS] = enabled
        }
    }

    // Location Settings
    suspend fun updateAutoCenterOnLocation(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_CENTER_ON_LOCATION] = enabled
        }
    }

    suspend fun updateContinuousLocationUpdates(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CONTINUOUS_LOCATION_UPDATES] = enabled
        }
    }

    // Parking Settings
    suspend fun updateParkingNotifications(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PARKING_NOTIFICATIONS] = enabled
        }
    }

    suspend fun updateShowFavoritesOnly(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_FAVORITES_ONLY] = enabled
        }
    }

    suspend fun updateAutoFilterAvailable(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_FILTER_AVAILABLE] = enabled
        }
    }

    // General Settings
    suspend fun updateDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE] = enabled
        }
    }

    suspend fun updateHapticFeedback(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAPTIC_FEEDBACK] = enabled
        }
    }

    suspend fun updateDataSaverMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DATA_SAVER_MODE] = enabled
        }
    }
}