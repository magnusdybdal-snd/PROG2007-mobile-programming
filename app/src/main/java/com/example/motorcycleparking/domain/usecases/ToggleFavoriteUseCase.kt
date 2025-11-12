package com.example.motorcycleparking.domain.usecases

import com.example.motorcycleparking.data.api.FirebaseRepo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val firebaseRepo: FirebaseRepo
) {

    // Event notifications built into the UseCase
    private val _favoritesChanged = MutableSharedFlow<Unit>(replay = 0)
    val favoritesChanged: SharedFlow<Unit> = _favoritesChanged.asSharedFlow()

    suspend operator fun invoke(spotId: String): Result<Unit> {
        return try {
            val userId = firebaseRepo.getCurrentUserId()
                ?: return Result.failure(Exception("User not logged in"))

            firebaseRepo.toggleFavorite(userId, spotId)
            _favoritesChanged.emit(Unit)    // Notifies that favorites has been updated
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}