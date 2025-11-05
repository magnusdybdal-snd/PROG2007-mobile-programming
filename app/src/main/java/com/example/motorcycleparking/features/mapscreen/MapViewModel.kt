package com.example.motorcycleparking.features.mapscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.motorcycleparking.domain.usecases.GetAllParkingSpotsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getAllParkingSpotsUseCase: GetAllParkingSpotsUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "MapViewModel"
    }

    private val _uiState = MutableStateFlow<MapUiState>(MapUiState(isLoading = true))
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    init {
        loadParkingSpots()
    }

    private fun loadParkingSpots() {
        viewModelScope.launch {
            getAllParkingSpotsUseCase()
                .onSuccess { parkingSpots ->
                    Log.d(TAG, "Received ${parkingSpots.size} parking spots from firestore")

                    _uiState.value = MapUiState(
                        isLoading = false,
                        spots = parkingSpots
                    )
                }
                .onFailure { exception ->
                    Log.e(TAG, "Error loading parking spots from firestore", exception)
                    _uiState.value = MapUiState(
                        isLoading = false,
                        errorMessage = exception.message ?: "Unknown error"
                    )
                }
        }
    }
}
