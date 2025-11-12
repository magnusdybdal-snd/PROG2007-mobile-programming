package com.example.motorcycleparking.data.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.motorcycleparking.domain.repositories.ISettingsRepository
import com.example.motorcycleparking.features.settingsscreen.SettingsState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepository @Inject constructor(
    private val context: Context
) : ISettingsRepository {

    private object PreferencesKeys {
        val SHOW_TRAFFIC = booleanPreferencesKey("show_traffic")
        val SHOW_MY_LOCATION_BUTTON = booleanPreferencesKey("show_my_location_button")
        val AUTO_CENTER_ON_LOCATION = booleanPreferencesKey("auto_center_on_location")
        val CONTINUOUS_LOCATION_UPDATES = booleanPreferencesKey("continuous_location_updates")
        val SHOW_FAVORITES_ONLY = booleanPreferencesKey("show_favorites_only")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    override val settingsFlow: Flow<SettingsState> = context.dataStore.data.map { preferences ->
        SettingsState(
            showTraffic = preferences[PreferencesKeys.SHOW_TRAFFIC] ?: true,
            showMyLocationButton = preferences[PreferencesKeys.SHOW_MY_LOCATION_BUTTON] ?: true,
            autoCenterOnLocation = preferences[PreferencesKeys.AUTO_CENTER_ON_LOCATION] ?: true,
            continuousLocationUpdates = preferences[PreferencesKeys.CONTINUOUS_LOCATION_UPDATES] ?: true,
            showFavoritesOnly = preferences[PreferencesKeys.SHOW_FAVORITES_ONLY] ?: false,
            darkMode = preferences[PreferencesKeys.DARK_MODE] ?: false,
        )
    }

    override suspend fun updateShowTraffic(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_TRAFFIC] = enabled
        }
    }

    override suspend fun updateMyLocationButton(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_MY_LOCATION_BUTTON] = enabled
        }
    }

    override suspend fun updateAutoCenterOnLocation(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTO_CENTER_ON_LOCATION] = enabled
        }
    }

    override suspend fun updateContinuousLocationUpdates(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CONTINUOUS_LOCATION_UPDATES] = enabled
        }
    }

    override suspend fun updateShowFavoritesOnly(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SHOW_FAVORITES_ONLY] = enabled
        }
    }

    override suspend fun updateDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_MODE] = enabled
        }
    }
}