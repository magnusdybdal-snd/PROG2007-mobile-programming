package com.example.motorcycleparking.features.favoiritescreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.motorcycleparking.core.core_ui.composable.PageHeading
import com.example.motorcycleparking.core.core_ui.composable.ParkingSpotCard
import com.example.workoutapp.core.core_ui.composable.LoadingStateView

/**
 * Displays user's favorite parking spots.
 *
 * @param modifier Applied to the root Column in Success and Error states
 * @param navController Navigation controller for potential navigation actions
 * @param viewModel Provides favorites state and handles favorite toggling
 */
@Composable
fun FavoriteScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Refresh favorites when screen becomes visible
    LaunchedEffect(Unit) {
        viewModel.loadFavorites()
    }

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
                Text(
                    text = "Error: $message",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        is FavoritesUiState.Success -> {
            val favorites = (uiState as FavoritesUiState.Success).favorites

            Column(modifier = modifier) {
                PageHeading(
                    modifier = Modifier,
                    text = "Favorites"
                )
                favorites.forEach { spot ->
                    ParkingSpotCard(
                        parkingSpot = spot,
                        isFavorite = true, // All spots on favorites screen are favorited
                        onFavoriteClick = {
                            spot.id?.let { viewModel.toggleFavorite(it) }
                        }
                    )
                }
            }
        }
    }
}
