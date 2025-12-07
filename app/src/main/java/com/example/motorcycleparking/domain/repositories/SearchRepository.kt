package com.example.motorcycleparking.domain.repositories

import com.example.motorcycleparking.domain.models.SearchHistoryItem
import com.example.motorcycleparking.domain.models.SearchResult

/**
 * Repository for managing search related operations
 */
interface SearchRepository {

    /**
     * Retrieves the users search history, ordered by newest first
     *
     * @return Result with a list of search history items, or an exception if user is not authenticated
     */
    suspend fun getSearchHistory(): Result<List<SearchHistoryItem>>

    /**
     * Search for parking spots that matches the query string
     * Performs case insensitive prefix matching on parking spot names
     *
     * @param query search query string
     * @return Result with list of matching parking spots, or empty list if no matches
     */
    suspend fun getTitlesinSearch(query: String): Result<List<SearchResult>>

    /**
     * Adds a parking spot to the users search history
     *
     * @param spotId The unique ID of the parking spot to be added
     * @param searchQuery The search query used
     * @return Result success if saved, otherwise failure
     */
    suspend fun addToSearchHistory(spotId: String, searchQuery: String): Result<Unit>
}