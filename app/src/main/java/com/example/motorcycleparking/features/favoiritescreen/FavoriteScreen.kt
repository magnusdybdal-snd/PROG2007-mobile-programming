package com.example.motorcycleparking.features.favoiritescreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.motorcycleparking.core.core_ui.composable.PageHeading
import com.example.motorcycleparking.core.core_ui.composable.ParkingSpotCard
import com.example.motorcycleparking.domain.models.ParkingSpot
import com.example.workoutapp.core.core_ui.composable.LoadingStateView

/**
 * Displays users favorite parkignspots.
 */
@Composable
fun FavoriteScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    cs: ColorScheme,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Mock data
    val mockFavorites = listOf(
        ParkingSpot(
            name = "Downtown Parking",
            id = "mock1",
            displayAddress = "123 Main Street, City Center",
            latitude = 60.7944800514934,
            longitude = 11.068259632114028,
            free = true,
            note = null
        ),
        ParkingSpot(
            name = "Park & Ride Lot",
            id = "mock2",
            displayAddress = "456 Oak Avenue, North Side",
            latitude = 38.00790342343918,
            longitude = 23.72066049567644,
            free = false,
            note = null
        )
    )

    when (uiState) {
        is FavoritesUiState.Loading -> {
            Column {
                PageHeading(
                    modifier = Modifier,
                    text = "Favorites"
                )
                LoadingStateView()
            }
        }

        is FavoritesUiState.Error -> {
            val message = (uiState as FavoritesUiState.Error).message

            Column(modifier = modifier) {
                PageHeading(
                    modifier = Modifier,
                    text = "Favorites"
                )

                // Print mock data anyway for now until API has it implemented
                Text(
                    text = "Error: $message (Showing mock data)",
                    modifier = Modifier.padding(16.dp)
                )

                mockFavorites.forEach { spot ->
                    ParkingSpotCard(parkingSpot = spot)
                }
            }
        }

        is FavoritesUiState.Success -> {
            val favorites = (uiState as FavoritesUiState.Success).favorites

            Column(modifier = modifier) {
                PageHeading(
                    modifier = Modifier,
                    text = "Favorites"
                )

                mockFavorites.forEach { spot ->
                    ParkingSpotCard(parkingSpot = spot)
                }
            }
        }
    }
}
