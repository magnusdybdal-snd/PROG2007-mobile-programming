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

/**
 * Manages authentication state and operations for the login screen.
 * Checks for existing session on initialization.
 */
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
        checkAuthState() // Check if user is already signed in
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

    /**
     * Authenticates user via Google Sign-In using OAuth credentials.
     *
     * @param account Google account obtained from sign-in flow
     */
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

    /**
     * Authenticates user with email and password credentials.
     *
     * @param email User's email address
     * @param password User's password
     */
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

    /**
     * Clears error message from state after toast has been displayed.
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}