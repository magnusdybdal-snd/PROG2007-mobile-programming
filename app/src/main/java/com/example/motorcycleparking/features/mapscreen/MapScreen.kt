package com.example.motorcycleparking.features.mapscreen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.motorcycleparking.R
import com.example.motorcycleparking.core.core_ui.composable.AddParkingSpotBottomSheet
import com.example.motorcycleparking.core.core_ui.composable.GoogleMapView
import com.example.motorcycleparking.core.core_ui.composable.ParkingSpotCard
import com.example.motorcycleparking.core.core_ui.composable.SimpleSearchBar
import com.example.motorcycleparking.core.core_ui.composable.rememberScaledMarker
import com.example.motorcycleparking.domain.mockNames
import com.example.motorcycleparking.features.settingsscreen.SettingsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: MapViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val textFieldState = remember { TextFieldState() }
    val uiState by viewModel.uiState.collectAsState()
    val favoriteIds by viewModel.favoriteIds.collectAsState()
    var searchResults by remember { mutableStateOf(mockNames) }
    var selectedSpot by remember { mutableStateOf<com.example.motorcycleparking.domain.models.ParkingSpot?>(null) }

    // State for add parking spot bottom sheet
    var showAddSpotSheet by remember { mutableStateOf(false) }
    var addSpotLocation by remember { mutableStateOf<LatLng?>(null) }

    val cameraPositionState = rememberCameraPositionState()
    val coroutineScope = rememberCoroutineScope()

    val motorcycleIcon = rememberScaledMarker(
        resId = R.drawable.mc_parking_icon,
        sizePx = 96    // try 96 or 128, can tweak
    )

    Box(modifier = modifier.fillMaxSize()) {
        // --- Google Maps Composable ---
        GoogleMapView(
            mapViewModel = viewModel,
            settingsViewModel = settingsViewModel,
            cameraPositionState = cameraPositionState,
            onMapLongClick = { latLng ->
                addSpotLocation = latLng
                showAddSpotSheet = true
            }
        ){
        uiState.spots.forEach { spot ->
            val markerPosition = LatLng(
                spot.latitude ?: 0.0,
                spot.longitude ?: 0.0
            )

            Marker(
                state = MarkerState(position = markerPosition),
                title = spot.name,
                icon = motorcycleIcon,
                snippet = "${spot.displayAddress}\n${if (spot.free ?: true ) "Free" else "Paid"}",
                onClick = {
                    selectedSpot = spot
                    // Animate camera to center on the marker (maintains current zoom)
                    coroutineScope.launch {
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLng(markerPosition),
                            durationMs = 500 // Animation duration
                        )
                    }
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

        // --- Parking Spot Card with dismiss functionality ---
        selectedSpot?.let { spot ->
            // Clickable overlay to dismiss card when tapping outside
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        selectedSpot = null
                    }
            )

            // Card wrapper with close button - positioned in center area near marker
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = 80.dp) // Offset below center to appear just under marker
            ) {
                ParkingSpotCard(
                    parkingSpot = spot,
                    isFavorite = spot.id in favoriteIds,
                    onFavoriteClick = {
                        viewModel.toggleFavorite(spot.id ?: "")
                    },
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* Prevent click through */ }
                )

                // Close button in top-right corner of card
                IconButton(
                    onClick = { selectedSpot = null },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-8).dp, y = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // --- Add Parking Spot Bottom Sheet ---
        if (showAddSpotSheet && addSpotLocation != null) {
            AddParkingSpotBottomSheet(
                onDismiss = {
                    showAddSpotSheet = false
                    addSpotLocation = null
                },
                onSave = { name, note, address, isFree ->
                    addSpotLocation?.let { location ->
                        viewModel.addParkingSpot(
                            name = name,
                            note = note,
                            address = address,
                            latitude = location.latitude,
                            longitude = location.longitude,
                            isFree = isFree,
                            onSuccess = {
                                Toast.makeText(
                                    context,
                                    "Parking spot added successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                showAddSpotSheet = false
                                addSpotLocation = null
                            },
                            onFailure = { error ->
                                Toast.makeText(
                                    context,
                                    "Failed to add spot: $error",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        )
                    }
                },
                latitude = addSpotLocation!!.latitude,
                longitude = addSpotLocation!!.longitude
            )
        }
    }
}
