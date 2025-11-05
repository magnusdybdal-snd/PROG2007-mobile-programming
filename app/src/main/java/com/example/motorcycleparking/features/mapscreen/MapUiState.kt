package com.example.motorcycleparking.features.mapscreen

import com.example.motorcycleparking.domain.models.ParkingSpot

// TODO: This is just an example, replace!
data class MapUiState(
    val isLoading: Boolean = false,
    val spots: List<ParkingSpot> = emptyList(),
    val selectedId: String? = null,
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