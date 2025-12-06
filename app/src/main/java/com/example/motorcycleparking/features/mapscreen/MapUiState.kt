package com.example.motorcycleparking.features.mapscreen

import com.example.motorcycleparking.domain.models.ParkingSpot

/**
 * Represents the UI state for the map screen.
 *
 * @property isLoading Indicates whether parking spots are being fetched
 * @property spots Currently loaded parking spots visible on the map
 * @property selectedId ID of the currently selected parking spot, if any
 * @property errorMessage Error message to display when spot loading fails
 */
data class MapUiState(
    val isLoading: Boolean = false,
    val spots: List<ParkingSpot> = emptyList(),
    val selectedId: String? = null,
    val errorMessage: String? = null
)
