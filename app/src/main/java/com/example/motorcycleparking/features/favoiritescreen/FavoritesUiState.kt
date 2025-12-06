package com.example.motorcycleparking.features.favoiritescreen

import com.example.motorcycleparking.domain.models.ParkingSpot

/**
 * Represents the UI state of the favorites screen.
 */
sealed interface FavoritesUiState {
    /**
     * Loading state while fetching favorites from the repository.
     */
    data object Loading: FavoritesUiState

    /**
     * Successfully loaded favorites state.
     *
     * @property favorites List of favorited parking spots for the current user
     */
    data class Success(
        val favorites: List<ParkingSpot>
    ) : FavoritesUiState

    /**
     * Error state when favorite loading fails.
     *
     * @property message Human-readable error description
     */
    data class Error(
        val message: String
    ) : FavoritesUiState
}