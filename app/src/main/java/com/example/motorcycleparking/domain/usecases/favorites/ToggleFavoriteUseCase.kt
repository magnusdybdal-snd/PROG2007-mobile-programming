package com.example.motorcycleparking.domain.usecases.favorites

import android.util.Log
import com.example.motorcycleparking.data.api.FirebaseRepo
import com.example.motorcycleparking.domain.repositories.FavoritesRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: FavoritesRepository
) {

    companion object {
        private const val TAG = "ToggleFavoriteUseCase"
    }

    // Event notifications built into the UseCase
    // replay = 1 ensures new collectors receive the last event even if they missed it
    private val _favoritesChanged = MutableSharedFlow<Unit>(replay = 1)
    val favoritesChanged: SharedFlow<Unit> = _favoritesChanged.asSharedFlow()

    suspend operator fun invoke(spotId: String): Result<Unit> {
        return repository.toggleFavorite(spotId)
            .onSuccess {
                _favoritesChanged.emit(Unit)    // Notifies that favorites has been updated
            }
            .onFailure { exception ->
                Log.e(TAG, "Failed to toggle favorite", exception)
            }
    }
}