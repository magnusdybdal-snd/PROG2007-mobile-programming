package com.example.motorcycleparking.domain.usecases.search

import com.example.motorcycleparking.domain.repositories.SearchRepository
import javax.inject.Inject

/**
 * Use case for adding a parking spot to the users search history
 */
class AddToSearchHistoryUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    /**
     * Adds a parking spot and search query to the users search history
     *
     * @param spotId The unique spot ID for the parking spot to be added
     * @param searchQuery The search query used in the search
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(spotId: String, searchQuery: String): Result<Unit> {
        return repository.addToSearchHistory(spotId, searchQuery)
    }
}