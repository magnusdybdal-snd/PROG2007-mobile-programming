package com.example.motorcycleparking.features.favoiritescreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.motorcycleparking.data.api.FirebaseRepo
import com.example.motorcycleparking.domain.usecases.favorites.GetFavoritesUseCase
import com.example.motorcycleparking.domain.usecases.favorites.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoritesUseCase: GetFavoritesUseCase,
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

    private fun observeFavoriteChanges() {
        viewModelScope.launch {
            toggleFavoriteUseCase.favoritesChanged.collect {
                loadFavorites()
            }
        }
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _uiState.value = FavoritesUiState.Loading

            val result = getFavoritesUseCase()

            result.fold(
                onSuccess = { favorites ->
                    Log.d(TAG, "Loaded ${favorites.size} favorites")
                    _uiState.value = FavoritesUiState.Success(favorites)
                },
                onFailure = { e ->
                    Log.e(TAG, "Failed to load favorites", e)
                    _uiState.value = FavoritesUiState.Error(
                        message = e.message ?: "Failed to load favorites"
                    )
                }
            )
        }
    }


    fun toggleFavorite(spotId: String) {
        viewModelScope.launch {
            toggleFavoriteUseCase(spotId)
                .onSuccess {
                }
                .onFailure { e ->
                    Log.e(TAG, "Failed to toggle favorite: $spotId", e)
                }
        }
    }
}
