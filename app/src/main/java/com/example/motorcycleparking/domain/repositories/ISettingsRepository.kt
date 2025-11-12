package com.example.motorcycleparking.domain.repositories

import com.example.motorcycleparking.features.settingsscreen.SettingsState
import kotlinx.coroutines.flow.Flow

interface ISettingsRepository {

    val settingsFlow: Flow<SettingsState>

    suspend fun updateShowTraffic(enabled: Boolean)
    suspend fun updateMyLocationButton(enabled: Boolean)
    suspend fun updateAutoCenterOnLocation(enabled: Boolean)
    suspend fun updateContinuousLocationUpdates(enabled: Boolean)
    suspend fun updateShowFavoritesOnly(enabled: Boolean)
    suspend fun updateDarkMode(enabled: Boolean)
}