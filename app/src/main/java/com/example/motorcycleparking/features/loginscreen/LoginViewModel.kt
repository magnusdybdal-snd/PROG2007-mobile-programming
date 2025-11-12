package com.example.motorcycleparking.features.loginscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.motorcycleparking.core.utils.encodeGeohash
import com.example.motorcycleparking.core.utils.getNeighbors
import com.example.motorcycleparking.data.api.FirebaseRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseRepo: FirebaseRepo
) : ViewModel() {

    companion object {
        private const val TAG = "LoginViewModel"
    }

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()


    fun signInWithEmailPassword(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState(
                isLoading = true
            )

            try {
                val uid = firebaseRepo.signInWithEmailPassword(email, password)
                firebaseRepo.toggleFavorite(uid, "Nm9NhSyRYP0ZpSOk15pU")
                Log.d(TAG, "Successfully logged in as $uid")
                _uiState.value = LoginUiState(
                    isSignedIn = true,
                    userId = uid
                )

                val lat = 37.7749   // San Francisco
                val lon = -122.42
                val precision = 7

                val geoHash = encodeGeohash(lat, lon, precision)
                println("Geohash: $geoHash")

                val neighbors = getNeighbors(geoHash, includeCenter = true)
                println("Neighbors (including center): $neighbors")
            } catch (e: Exception) {
                Log.e(TAG, "Sign in failed", e)
                _uiState.value = LoginUiState(
                    errorMessage = e.message ?: "Sign in failed"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}