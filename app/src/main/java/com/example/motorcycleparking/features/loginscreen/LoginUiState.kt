package com.example.motorcycleparking.features.loginscreen

/**
 * Represents the authentication state for the login screen.
 *
 * @property isLoading Indicates ongoing authentication operation
 * @property isSignedIn Triggers navigation to main app flow when true
 * @property userId Firebase user identifier once authenticated
 * @property errorMessage Error to display as toast; cleared after showing
 */
data class LoginUiState(
    val isLoading: Boolean = false,
    val isSignedIn: Boolean = false,
    val userId: String? = null,
    val errorMessage: String? = null
)