package com.example.motorcycleparking.domain.repositories

import com.example.motorcycleparking.domain.models.ParkingSpot

/**
 * Repository for managing search related operations
 */
interface SearchRepository {

    suspend fun getSearchHistory(): Result<Unit>

    suspend fun getTitlesinSearch(query: String): Result<Unit>

    suspend fun addToSearchHistory(): Result<Unit>
}