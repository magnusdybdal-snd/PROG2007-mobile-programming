package com.example.motorcycleparking.features.mapscreen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.motorcycleparking.core.core_navigation.Routes
import com.example.motorcycleparking.core.core_ui.composable.GoogleMapView
import com.example.motorcycleparking.core.core_ui.composable.SimpleSearchBar
import com.example.motorcycleparking.domain.mockNames


/**
 * Displays Map screen
 */
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val cs = MaterialTheme.colorScheme
    val textFieldState = remember { TextFieldState() }
    var searchResults by remember { mutableStateOf(mockNames) }

    // --- Google Maps Compoasable ---
    GoogleMapView()
    // --- Search Bar Compoasable---
    // TODO implements actual searchability outside mockdata
    SimpleSearchBar(
        textFieldState = textFieldState,
        onSearch = { query ->
            searchResults = if (query.isBlank()) {
                mockNames
            } else {
                mockNames.filter { it.contains(query, ignoreCase = true) }
            }
        },
        searchResults = searchResults
    )
    Button( // TODO remove, this is a temporary button to get to the loginscreen.
        onClick = {
            // 👇 Navigate to your login route
            navController.navigate(route = Routes.LOGIN)
        },
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Go to Google Login")
    }
}
