package com.example.motorcycleparking.features.favoiritescreen

import android.util.Log
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

    private fun observeFavoriteChanges() {
        viewModelScope.launch {
            toggleFavoriteUseCase.favoritesChanged.collect {
                Log.d(TAG, "Favorites changed - Reloading")
                loadFavorites()
            }
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _uiState.value = FavoritesUiState.Loading

            try {
                val userId = firebaseRepo.getCurrentUserId()
                    ?: throw Exception("User not logged in")
                val favorites = firebaseRepo.getFavorites(userId)
                _uiState.value = FavoritesUiState.Success(favorites)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load favorites", e)
                _uiState.value = FavoritesUiState.Error(message = e.message ?: "Failed to load favorites")
            }
        }
    }

    fun toggleFavorite(spotId: String) {
        viewModelScope.launch {
            try {
                val userId = firebaseRepo.getCurrentUserId()
                    ?: throw Exception("User not logged in")
                firebaseRepo.toggleFavorite(userId, spotId)
                loadFavorites() // Refresh
            } catch (e: Exception) {
                Log.e(TAG, "Failed to toggle favorite: $spotId", e)
            }
        }
    }
}
