package com.example.motorcycleparking.features.favoiritescreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.motorcycleparking.data.api.FirebaseRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val firebaseRepo: FirebaseRepo
) : ViewModel() {

    companion object {
        private const val TAG = "FavoritesViewModel"
    }

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    //TODO: Implement something when mock data from screen is replaced
    private fun loadFavorites() {
        viewModelScope.launch {
            _uiState.value = FavoritesUiState.Loading

            try {
                //  val favorites = firebaseRepo.getFavorites()
                // _uiState.value = FavoritesUiState(favorites = favorites)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load favorites", e)
                _uiState.value =
                    FavoritesUiState(errorMessage = e.message ?: "Failed to load favorites")
            }
        }
    }

    fun retry() {
        loadFavorites()
    }
}
