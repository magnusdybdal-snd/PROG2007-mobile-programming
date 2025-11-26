package com.example.motorcycleparking.features.loginscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.motorcycleparking.data.api.FirebaseRepo
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
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

    fun signInWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            _uiState.value = LoginUiState(
                isLoading = true
            )

            try {
                val uid = firebaseRepo.signInWithGoogle(account)
                _uiState.value = LoginUiState(
                    isSignedIn = true,
                    userId = uid
                )
            } catch (e: Exception) {
                Log.e(TAG, "Google sign in failed", e)
                _uiState.value = LoginUiState(
                    errorMessage = e.message ?: "Google sign in failed"
                )
            }
        }
    }

    fun signInWithEmailPassword(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState(
                isLoading = true
            )

            try {
                val uid = firebaseRepo.signInWithEmailPassword(email, password)
                _uiState.value = LoginUiState(
                    isSignedIn = true,
                    userId = uid
                )

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