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

/**
 * Represents the complete application settings state.
 *
 * @property showTraffic Enables real-time traffic overlay on map
 * @property showMyLocationButton Displays button to recenter map on user location
 * @property autoCenterOnLocation Automatically centers map when user location updates
 * @property continuousLocationUpdates Enables persistent location tracking instead of one-time fetch
 * @property showFavoritesOnly Filters parking spots to display only user-saved favorites
 * @property darkMode Enables app-wide dark theme
 */
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

/**
 * Manages application settings with persistent storage and Firebase authentication.
 *
 * @param context Application context for accessing SettingsRepository
 * @param firebaseRepo Handles user sign-out operations
 */
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
    /** Updates traffic overlay visibility and persists to repository. */
    fun updateShowTraffic(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateShowTraffic(enabled)
        }
    }

    /** Updates my location button visibility and persists to repository. */
    fun updateMyLocationButton(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateMyLocationButton(enabled)
        }
    }

    // Location Settings
    /** Updates auto-center behavior and persists to repository. */
    fun updateAutoCenterOnLocation(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateAutoCenterOnLocation(enabled)
        }
    }

    /** Updates continuous location tracking mode and persists to repository. */
    fun updateContinuousLocationUpdates(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateContinuousLocationUpdates(enabled)
        }
    }

    // Parking Settings
    /** Updates favorites filter and persists to repository. */
    fun updateShowFavoritesOnly(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateShowFavoritesOnly(enabled)
        }
    }

    // General Settings
    /** Updates theme preference and persists to repository. */
    fun updateDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateDarkMode(enabled)
        }
    }

    /** Signs out current user. */
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