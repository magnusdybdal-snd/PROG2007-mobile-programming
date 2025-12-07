package com.example.motorcycleparking.domain.repositories

import com.example.motorcycleparking.domain.models.ParkingSpot

/**
 * Repository interface for managing favorites
 */
interface FavoritesRepository {

    /**
     * Retrieves all parking spots marked as favorite by the user
     *
     * @return Result with list with favorite parking spots, or exception on failure
     */
    suspend fun getFavoriteSpots(): Result<List<ParkingSpot>>

    /**
     * Toggles a parking spot as favorite, or removes it if it already is a favorite
     *
     * @param spotId The ID of the parking spot to add / remove from favorites
     * @return Result indicating success or failure
     */
    suspend fun toggleFavorite(spotId: String): Result<Unit>
}