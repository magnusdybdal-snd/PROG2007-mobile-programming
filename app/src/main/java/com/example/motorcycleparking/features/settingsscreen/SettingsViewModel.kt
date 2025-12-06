package com.example.motorcycleparking.features.settingsscreen

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.motorcycleparking.data.repositories.SettingsRepository
import com.example.motorcycleparking.domain.usecases.authentication.SignOutUseCase
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
    val showMyLocationButton: Boolean = true,

    // Location Settings
    val autoCenterOnLocation: Boolean = true,
    val continuousLocationUpdates: Boolean = true,

    // Parking Settings
    val showFavoritesOnly: Boolean = false,

    // General Settings
    val darkMode: Boolean = false,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val signOutUseCase: SignOutUseCase,
) : ViewModel() {

    companion object {
        private const val TAG = "SettingsViewModel"
    }

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

    fun updateMyLocationButton(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateMyLocationButton(enabled)
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
    fun updateShowFavoritesOnly(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateShowFavoritesOnly(enabled)
        }
    }

    // General Settings
    fun updateDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateDarkMode(enabled)
        }
    }

    fun signOut(){
        viewModelScope.launch {
            signOutUseCase()
                .onSuccess {
                    Log.d(TAG, "User signed out successfully")
                }
                .onFailure { exception ->
                    Log.e(TAG, "Sign out failed", exception)
                }
        }
    }
}