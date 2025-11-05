package com.example.motorcycleparking.features.favoiritescreen

import com.example.motorcycleparking.domain.models.ParkingSpot

sealed interface FavoritesUiState {
    data object Loading: FavoritesUiState

    data class Success(
        val favorites: List<ParkingSpot>
    ) : FavoritesUiState

    data class Error(
        val message: String
    ) : FavoritesUiState
}