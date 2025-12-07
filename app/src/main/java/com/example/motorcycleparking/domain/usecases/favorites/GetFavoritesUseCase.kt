package com.example.motorcycleparking.domain.usecases.favorites

import com.example.motorcycleparking.domain.models.ParkingSpot
import com.example.motorcycleparking.domain.repositories.FavoritesRepository
import javax.inject.Inject

/**
 * Use case for retrieving the users favorite parking spots
 */
class GetFavoritesUseCase @Inject constructor(
    private val repository: FavoritesRepository
) {

    /**
     * Gets the users favorite parking spots
     *
     * @return Result with list of favorite parking spots, or exception on failure
     */
    suspend operator fun invoke(): Result<List<ParkingSpot>> {
        return repository.getFavoriteSpots()
    }
}