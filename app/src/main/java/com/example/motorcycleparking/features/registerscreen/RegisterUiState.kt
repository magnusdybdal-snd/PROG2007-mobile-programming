package com.example.motorcycleparking.features.registerscreen

import com.example.motorcycleparking.domain.models.ParkingSpot

// TODO: This is just an example, replace!
data class RegisterUiState(
    val isLoading: Boolean = false,
    val userId: String? = null,
    val errorMessage: String? = null
)

// TODO: Maybe use sealed interface??
//sealed interface MapUiState {
//    data object Loading : MapUiState
//
//    data class Success(
//        val spots: List<ParkingSpot>,
//        val selectedId: String? = null
//    ) : MapUiState
//
//    data class Error(
//        val message: String
//    ) : MapUiState
//}