package com.example.motorcycleparking.domain.usecases

import android.util.Log
import com.example.motorcycleparking.data.api.FirebaseRepo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val firebaseRepo: FirebaseRepo
) {

    // Event notifications built into the UseCase
    // replay = 1 ensures new collectors receive the last event even if they missed it
    private val _favoritesChanged = MutableSharedFlow<Unit>(replay = 1)
    val favoritesChanged: SharedFlow<Unit> = _favoritesChanged.asSharedFlow()

    suspend operator fun invoke(spotId: String): Result<Unit> {
        return try {
            Log.d("ToggleFavoriteUseCase", "🔄 Toggling favorite: $spotId")
            firebaseRepo.toggleFavorite(spotId)
            Log.d("ToggleFavoriteUseCase", "📢 Emitting favoritesChanged event")
            _favoritesChanged.emit(Unit)    // Notifies that favorites has been updated
            Log.d("ToggleFavoriteUseCase", "✅ Event emitted successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ToggleFavoriteUseCase", "❌ Failed to toggle favorite", e)
            Result.failure(e)
        }
    }
}