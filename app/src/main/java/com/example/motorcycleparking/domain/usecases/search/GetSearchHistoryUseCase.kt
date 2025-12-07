package com.example.motorcycleparking.domain.usecases.search

import com.example.motorcycleparking.domain.models.SearchHistoryItem
import com.example.motorcycleparking.domain.repositories.SearchRepository
import javax.inject.Inject

/**
 * Use case for retrieving a users search history
 */
class GetSearchHistoryUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    /**
     * Fetches the users search history
     *
     * @return Result with a list of the users last searches ordered by last used
     */
    suspend operator fun invoke(): Result<List<SearchHistoryItem>> {
        return repository.getSearchHistory()
    }
}