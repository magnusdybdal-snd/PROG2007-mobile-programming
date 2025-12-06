package com.example.motorcycleparking.features.favoiritescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.motorcycleparking.data.api.FirebaseRepo
import com.example.motorcycleparking.domain.usecases.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Manages favorites state with automatic reloading on favorite changes.
 *
 * @property firebaseRepo Repository for fetching user's favorite parking spots
 * @property toggleFavoriteUseCase Use case for toggling favorites and emitting change events
 */
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val firebaseRepo: FirebaseRepo,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "FavoritesViewModel"
    }

    private val _uiState = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Loading)
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
        observeFavoriteChanges()
    }

    /**
     * Observes favorite change events and triggers reload to keep UI synchronized.
     */
    private fun observeFavoriteChanges() {
        viewModelScope.launch {
            toggleFavoriteUseCase.favoritesChanged.collect {
                loadFavorites()
            }
        }
    }

    /**
     * Fetches current user's favorites and updates UI state accordingly.
     */
    fun loadFavorites() {
        viewModelScope.launch {
            _uiState.value = FavoritesUiState.Loading

            val result = firebaseRepo.getFavorites()

            result.fold(
                onSuccess = { favorites ->
                    _uiState.value = FavoritesUiState.Success(favorites)
                },
                onFailure = { e ->
                    _uiState.value = FavoritesUiState.Error(
                        message = e.message ?: "Failed to load favorites"
                    )
                }
            )
        }
    }


    /**
     * Toggles favorite status for the given parking spot.
     * Automatic UI reload is handled by observeFavoriteChanges().
     *
     * @param spotId Parking spot ID to toggle favorite status for
     */
    fun toggleFavorite(spotId: String) {
        viewModelScope.launch {
            toggleFavoriteUseCase(spotId)
                .onSuccess {
                }
                .onFailure { e ->
                }
        }
    }
}
