package com.example.motorcycleparking.domain.usecases.search

import com.example.motorcycleparking.domain.models.SearchResult
import com.example.motorcycleparking.domain.repositories.SearchRepository
import javax.inject.Inject

/**
 * Use case for getting a list of search results / parking spots
 */
class SearchParkingSpotsUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    /**
     * Retrieves a list of matching parking spots to the search query
     *
     * @param searchQuery the search query used to fetch the results
     * @return Result with a list of matching parking spots, or empty list if none
     */
    suspend operator fun invoke(searchQuery: String): Result<List<SearchResult>> {
        return repository.getTitlesinSearch(searchQuery)
    }
}