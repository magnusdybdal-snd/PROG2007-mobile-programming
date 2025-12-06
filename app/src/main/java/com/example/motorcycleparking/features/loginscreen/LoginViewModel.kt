package com.example.motorcycleparking.features.loginscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.motorcycleparking.data.api.FirebaseRepo
import com.example.motorcycleparking.domain.usecases.authentication.GetCurrentUserUseCase
import com.example.motorcycleparking.domain.usecases.authentication.SignInWithEmailPasswordUseCase
import com.example.motorcycleparking.domain.usecases.authentication.SignInWithGoogleUseCase
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signInWithEmailPasswordUseCase: SignInWithEmailPasswordUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "LoginViewModel"
    }

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        // Check if user is already signed in
        checkAuthState()
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            val userId = getCurrentUserUseCase()
            if (userId != null) {
                Log.d(TAG, "Found user with 'userId': $userId")
                _uiState.value = LoginUiState(
                    isSignedIn = true,
                    userId = userId
                )
            }
        }
    }

    fun signInWithGoogle(account: GoogleSignInAccount) {
        viewModelScope.launch {
            _uiState.value = LoginUiState(
                isLoading = true
            )

            signInWithGoogleUseCase(account)
                .onSuccess { uid ->
                    _uiState.value = LoginUiState(
                        isSignedIn = true,
                        userId = uid
                    )
                }
                .onFailure { exception ->
                    Log.e(TAG, "Google sign in failed", exception)
                    _uiState.value = LoginUiState(
                        errorMessage = exception.message ?: "Google sign in failed"
                    )
                }
        }
    }

    fun signInWithEmailPassword(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState(
                isLoading = true
            )

            signInWithEmailPasswordUseCase(email, password)
                .onSuccess { uid ->
                    _uiState.value = LoginUiState(
                        isSignedIn = true,
                        userId = uid
                    )
                }
                .onFailure { exception ->
                    Log.e(TAG, "Email sign in failed", exception)
                    _uiState.value = LoginUiState(
                        errorMessage = exception.message ?: "Email sign in failed"
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}