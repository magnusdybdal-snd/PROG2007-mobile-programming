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
        val SHOW_MY_LOCATION_BUTTON = booleanPreferencesKey("show_my_location_button")

        // Location Settings
        val AUTO_CENTER_ON_LOCATION = booleanPreferencesKey("auto_center_on_location")
        val CONTINUOUS_LOCATION_UPDATES = booleanPreferencesKey("continuous_location_updates")

        // Parking Settings
        val SHOW_FAVORITES_ONLY = booleanPreferencesKey("show_favorites_only")

        // General Settings
        val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    // Flow to observe settings changes
    val settingsFlow: Flow<SettingsState> = context.dataStore.data.map { preferences ->
        SettingsState(
            // Map Settings
            showTraffic = preferences[PreferencesKeys.SHOW_TRAFFIC] ?: true,
            showMyLocationButton = preferences[PreferencesKeys.SHOW_MY_LOCATION_BUTTON] ?: true,

            // Location Settings
            autoCenterOnLocation = preferences[PreferencesKeys.AUTO_CENTER_ON_LOCATION] ?: true,
            continuousLocationUpdates = preferences[PreferencesKeys.CONTINUOUS_LOCATION_UPDATES] ?: true,

            // Parking Settings
            showFavoritesOnly = preferences[PreferencesKeys.SHOW_FAVORITES_ONLY] ?: false,

            // General Settings
            darkMode = preferences[PreferencesKeys.DARK_MODE] ?: false,
        )
    }

    // Map Settings
    suspend fun updateShowTraffic(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_TRAFFIC] = enabled
        }
    }

    suspend fun updateMyLocationButton(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_MY_LOCATION_BUTTON] = enabled
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

    suspend fun updateShowFavoritesOnly(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_FAVORITES_ONLY] = enabled
        }
    }

    // General Settings
    suspend fun updateDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE] = enabled
        }
    }
}