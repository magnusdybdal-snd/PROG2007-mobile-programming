package com.example.motorcycleparking.features.mapscreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.motorcycleparking.R
import com.example.motorcycleparking.core.core_navigation.Routes
import com.example.motorcycleparking.core.core_ui.composable.GoogleMapView
import com.example.motorcycleparking.core.core_ui.composable.SimpleSearchBar
import com.example.motorcycleparking.core.core_ui.composable.rememberScaledMarker
import com.example.motorcycleparking.domain.mockNames
import com.example.motorcycleparking.features.settingsscreen.SettingsViewModel
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: MapViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel
) {
    val textFieldState = remember { TextFieldState() }
    val uiState by viewModel.uiState.collectAsState()
    var searchResults by remember { mutableStateOf(mockNames) }

    val motorcycleIcon = rememberScaledMarker(
        resId = R.drawable.mc_parking_icon,
        sizePx = 96    // try 96 or 128, can tweak
    )

    Box(modifier = modifier.fillMaxSize()) {
        // --- Google Maps Composable ---
        GoogleMapView(
            mapViewModel = viewModel,
            settingsViewModel = settingsViewModel
        ){
            uiState.spots.forEach { spot ->
                Marker(
                    state = MarkerState(
                        position = com.google.android.gms.maps.model.LatLng(
                            spot.latitude ?: 0.0,
                            spot.longitude ?: 0.0
                        )
                    ),
                    title = spot.name,
                    icon = motorcycleIcon,
                    snippet = "${spot.displayAddress}\n${if (spot.free ?: true ) "Free" else "Paid"}",
                    onClick = { marker ->
                        marker.showInfoWindow()
                        true
                    }
                )
            }
        }

        // --- Search Bar Composable ---
        SimpleSearchBar(
            textFieldState = textFieldState,
            onSearch = { query ->
                searchResults = if (query.isBlank()) {
                    mockNames
                } else {
                    mockNames.filter { it.contains(query, ignoreCase = true) }
                }
            },
            searchResults = searchResults,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // TODO remove, this is a temporary button
        Button(
            onClick = {
                navController.navigate(route = Routes.LOGIN)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text("Go to Google Login")
        }
    }
}
