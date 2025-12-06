package com.example.motorcycleparking.features.registerscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.motorcycleparking.domain.usecases.authentication.RegisterUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Manages user registration state and authentication flow.
 *
 * @param firebaseRepo Repository for Firebase authentication operations
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "RegisterViewModel"
    }

    private val _uiState = MutableStateFlow(RegisterUiState())

    /**
     * Exposes current registration state including loading status, user ID, and errors.
     */
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    /**
     * Creates a new user account with Firebase authentication.
     * Updates UI state with loading status, success (user ID), or error message.
     *
     * @param email User's email address for authentication
     * @param password User's password for authentication
     */
    fun createUser(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = RegisterUiState(
                isLoading = true
            )

            registerUserUseCase(email, password)
                .onSuccess { uid ->
                    _uiState.value = RegisterUiState(
                        userId = uid
                    )
                }
                .onFailure { exception ->
                    Log.e(TAG, "User registration failed", exception)
                    _uiState.value = RegisterUiState(
                        errorMessage = exception.message ?: "Registration failed"
                    )
                }
        }
    }
    /**
     * Clears the error message from UI state after it has been displayed to the user.
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}