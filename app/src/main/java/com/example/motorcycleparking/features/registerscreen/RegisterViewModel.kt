package com.example.motorcycleparking.features.registerscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.motorcycleparking.data.api.FirebaseRepo
import com.example.motorcycleparking.features.loginscreen.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseRepo: FirebaseRepo
) : ViewModel() {

    companion object {
        private const val TAG = "RegisterViewModel"
    }

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun createUser(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = RegisterUiState(
                isLoading = true
            )

            try {
                val user = firebaseRepo.createUser(email, password)
                Log.d(TAG, "Successfully created user ${user?.uid}")
                _uiState.value = RegisterUiState(
                    userId = user?.uid
                )
            } catch (e: Exception) {
                Log.e(TAG, "Sign in failed", e)
                _uiState.value = RegisterUiState(
                    errorMessage = e.message ?: "Registration failed"
                )
            }
        }
    }
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}