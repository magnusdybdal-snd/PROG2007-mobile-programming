package com.example.motorcycleparking.features.settingsscreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// Data class for all settings
data class SettingsState(
    // Map Settings
    val showTraffic: Boolean = true,
    val show3DBuildings: Boolean = true,
    val showMyLocationButton: Boolean = true,
    val showZoomControls: Boolean = true,

    // Location Settings
    val autoCenterOnLocation: Boolean = true,
    val continuousLocationUpdates: Boolean = true,

    // Parking Settings
    val parkingNotifications: Boolean = true,
    val showFavoritesOnly: Boolean = false,
    val autoFilterAvailable: Boolean = true,

    // General Settings
    val darkMode: Boolean = false,
    val hapticFeedback: Boolean = true,
    val dataSaverMode: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val repository = SettingsRepository(context)

    // Observe settings from repository
    val settingsState: StateFlow<SettingsState> = repository.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsState()
        )

    // Map Settings
    fun updateShowTraffic(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateShowTraffic(enabled)
        }
    }

    fun update3DBuildings(enabled: Boolean) {
        viewModelScope.launch {
            repository.update3DBuildings(enabled)
        }
    }

    fun updateMyLocationButton(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateMyLocationButton(enabled)
        }
    }

    fun updateZoomControls(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateZoomControls(enabled)
        }
    }

    // Location Settings
    fun updateAutoCenterOnLocation(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateAutoCenterOnLocation(enabled)
        }
    }

    fun updateContinuousLocationUpdates(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateContinuousLocationUpdates(enabled)
        }
    }

    // Parking Settings
    fun updateParkingNotifications(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateParkingNotifications(enabled)
        }
    }

    fun updateShowFavoritesOnly(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateShowFavoritesOnly(enabled)
        }
    }

    fun updateAutoFilterAvailable(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateAutoFilterAvailable(enabled)
        }
    }

    // General Settings
    fun updateDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateDarkMode(enabled)
        }
    }

    fun updateHapticFeedback(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateHapticFeedback(enabled)
        }
    }

    fun updateDataSaverMode(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateDataSaverMode(enabled)
        }
    }
}