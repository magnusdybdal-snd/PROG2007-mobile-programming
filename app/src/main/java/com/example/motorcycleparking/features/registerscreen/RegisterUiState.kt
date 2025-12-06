package com.example.motorcycleparking.features.registerscreen

/**
 * Represents the registration screen state.
 *
 * @property isLoading True when user creation is in progress
 * @property userId Set to user ID on successful registration, triggers navigation to login
 * @property errorMessage Contains error details when registration fails, displayed via toast
 */
data class RegisterUiState(
    val isLoading: Boolean = false,
    val userId: String? = null,
    val errorMessage: String? = null
)