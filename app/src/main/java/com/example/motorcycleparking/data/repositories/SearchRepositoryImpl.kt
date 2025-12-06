package com.example.motorcycleparking.data.repositories

import com.example.motorcycleparking.data.api.FirebaseRepo
import com.example.motorcycleparking.domain.models.ParkingSpot
import com.example.motorcycleparking.domain.repositories.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val firebaseRepo: FirebaseRepo
) : SearchRepository {
    override suspend fun getSearchHistory(): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun addToSearchHistory(): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getTitlesinSearch(query: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getParkingById(spotId: String): Result<ParkingSpot> {
        TODO("Not yet implemented")
    }
}