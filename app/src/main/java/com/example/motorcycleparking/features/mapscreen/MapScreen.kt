package com.example.motorcycleparking.features.mapscreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.motorcycleparking.core.core_navigation.Routes
import com.example.motorcycleparking.core.core_ui.composable.GoogleMapView
import com.example.motorcycleparking.core.core_ui.composable.SimpleSearchBar
import com.example.motorcycleparking.domain.mockNames
import com.example.motorcycleparking.features.settingsscreen.SettingsViewModel

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: MapViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val textFieldState = remember { TextFieldState() }
    var searchResults by remember { mutableStateOf(mockNames) }

    Box(modifier = modifier.fillMaxSize()) {
        // --- Google Maps Composable ---
        GoogleMapView(
            mapViewModel = viewModel,
            settingsViewModel = settingsViewModel
        )

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