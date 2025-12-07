package com.example.motorcycleparking.data.repositories

import android.util.Log
import com.example.motorcycleparking.data.api.FirebaseRepo
import com.example.motorcycleparking.domain.models.ParkingSpot
import com.example.motorcycleparking.domain.repositories.FavoritesRepository
import javax.inject.Inject

/**
 * Implementation of the FavoritesRepository
 *
 * This class acts as the data layer implementation of the repository and delegates operations to
 * the FirebaseRepo
 */
class FavoritesRepositoryImpl  @Inject constructor(
    private val firebaseRepo: FirebaseRepo
) : FavoritesRepository {

    companion object {
        private const val TAG = "FavoritesRepositoryImpl"
    }

    override suspend fun getFavoriteSpots(): Result<List<ParkingSpot>> {
        return firebaseRepo.getFavorites()
    }

    override suspend fun toggleFavorite(spotId: String): Result<Unit> {
        return try {
            firebaseRepo.toggleFavorite(spotId)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to toggle favorite for parking spot: $spotId", e)
            Result.failure(e)
        }
    }
}