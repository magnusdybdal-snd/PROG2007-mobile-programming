package com.example.motorcycleparking.features.loginscreen

data class LoginUiState(
    val isLoading: Boolean = false,
    val isSignedIn: Boolean = false,
    val userId: String? = null,
    val errorMessage: String? = null
)