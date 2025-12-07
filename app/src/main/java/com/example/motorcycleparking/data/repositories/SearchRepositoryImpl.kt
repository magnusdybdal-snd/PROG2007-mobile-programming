package com.example.motorcycleparking.data.repositories

import com.example.motorcycleparking.data.api.FirebaseRepo
import com.example.motorcycleparking.data.api.dto.HistoryDto
import com.example.motorcycleparking.data.api.dto.SearchDto
import com.example.motorcycleparking.domain.models.ParkingSpot
import com.example.motorcycleparking.domain.models.SearchHistoryItem
import com.example.motorcycleparking.domain.models.SearchResult
import com.example.motorcycleparking.domain.repositories.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val firebaseRepo: FirebaseRepo
) : SearchRepository {
    override suspend fun getSearchHistory(): Result<List<SearchHistoryItem>> {
        return try {
            val dtoResult = firebaseRepo.getSearchHistory()

            dtoResult.map { historyDtoList ->
                historyDtoList.map { it.toDomain() }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTitlesinSearch(query: String): Result<List<SearchResult>> {
        return try {
            val dtoResult = firebaseRepo.getTitlesInSearch(query)

            dtoResult.map { searchDtoList ->
                searchDtoList.map { it.toDomain() }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun addToSearchHistory(spotId: String, searchQuery: String): Result<Unit> {
        return try {
            firebaseRepo.addToSearchHistory(spotId, searchQuery)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Extension functions to map from dto to domain
 */

fun HistoryDto.toDomain(): SearchHistoryItem {
    return SearchHistoryItem(
        parkingSpotId = this.parkingId,
        searchQuery = this.search,
        timestampMillis = this.timestamp.toDate().time // Convert Firebase timestamp to Long
    )
}

fun SearchDto.toDomain(): SearchResult {
    return SearchResult(
        spotId = this.spotId,
        name = this.name
    )
}