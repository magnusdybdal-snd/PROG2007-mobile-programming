package com.example.motorcycleparking.core.core_ui.composable

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.motorcycleparking.features.mapscreen.MapViewModel
import com.example.motorcycleparking.features.settingsscreen.SettingsViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Google Maps composable with location tracking, permissions, and settings integration.a
 *
 * Handles ACCESS_FINE_LOCATION permission requests, manages continuous location updates,
 * auto-centers camera on user location, and loads parking spots when camera stops moving.
 *
 * @param mapViewModel Manages map state, location tracking, and parking spot loading; coordinates between map UI and data layer
 * @param settingsViewModel Provides user preferences that control map behavior (auto-center, traffic, dark mode, continuous updates)
 * @param cameraPositionState Controls map camera position; state is monitored to detect manual user movements and trigger parking spot loading
 * @param onMapLongClick Invoked when user long-clicks the map; receives the tapped LatLng coordinates for custom handling
 * @param content Composable lambda for rendering markers and overlays on the map; invoked within GoogleMap scope
 */
@Composable
fun GoogleMapView(
    mapViewModel: MapViewModel,
    settingsViewModel: SettingsViewModel,
    cameraPositionState: CameraPositionState = rememberCameraPositionState(),
    onMapLongClick: (LatLng) -> Unit = {},
    content: @Composable () -> Unit  // Changed from 'function' to 'content' (better naming)
) {
    // Obtain the current context
    val context = LocalContext.current
    // Observe the user's location from the ViewModel
    val userLocation by mapViewModel.userLocation
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val cs = MaterialTheme.colorScheme

    // Observe settings state - using collectAsStateWithLifecycle
    val settingsState by settingsViewModel.settingsState.collectAsStateWithLifecycle()

    // Handle permission requests for accessing fine location
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Fetch the user's location if permission is granted
            mapViewModel.fetchUserLocation(context, fusedLocationClient)

            // Start continuous location updates only if enabled in settings
            if (settingsState.continuousLocationUpdates) {
                mapViewModel.startLocationUpdates(context, fusedLocationClient)
            }
        } else {
            // Handle the case when permission is denied
            Timber.e("Location permission was denied by the user.")
        }
    }

    // Track if permission is granted
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Request the location permission when the composable is launched
    LaunchedEffect(Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            // Check if the location permission is already granted
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                // Fetch the user's location
                mapViewModel.fetchUserLocation(context, fusedLocationClient)

                // Start continuous location updates only if enabled in settings
                if (settingsState.continuousLocationUpdates) {
                    mapViewModel.startLocationUpdates(context, fusedLocationClient)
                }
            }

            else -> {
                // Request the location permission if it has not been granted
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    // React to changes in continuous location updates setting
    LaunchedEffect(settingsState.continuousLocationUpdates) {
        if (hasLocationPermission) {
            if (settingsState.continuousLocationUpdates) {
                mapViewModel.startLocationUpdates(context, fusedLocationClient)
            } else {
                mapViewModel.stopLocationUpdates(fusedLocationClient)
            }
        }
    }

    // Stop location updates when composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            mapViewModel.stopLocationUpdates(fusedLocationClient)
        }
    }

    // Track if user has manually moved the camera
    var hasUserMovedCamera by remember { mutableStateOf(false) }
    var isAutoCentering by remember { mutableStateOf(false) }

    // Critical: Must distinguish between programmatic (auto-center) and user-initiated camera movements
    // to prevent auto-centering from interfering with user exploration
    LaunchedEffect(cameraPositionState.isMoving) {
        // If camera is moving and it's not due to auto-centering, user moved it manually
        if (cameraPositionState.isMoving && !isAutoCentering) {
            hasUserMovedCamera = true
        }
    }

    // Reset manual movement flag when auto-center setting is toggled on
    LaunchedEffect(settingsState.autoCenterOnLocation) {
        if (settingsState.autoCenterOnLocation) {
            hasUserMovedCamera = false
        }
    }

    // Update camera position when user location changes and auto-center is enabled
    // ..but only if the user hasn't manually moved the camera
    // Note: Uses direct position assignment (not animate) for instant tracking during continuous updates
    LaunchedEffect(userLocation, settingsState.autoCenterOnLocation) {
        if (settingsState.autoCenterOnLocation && !hasUserMovedCamera) {
            userLocation?.let {
                isAutoCentering = true
                cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
                isAutoCentering = false
            }
        }
    }

    // Load parking spots when camera stops moving (for panning/zooming)
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            val center = cameraPositionState.position.target
            val zoom = cameraPositionState.position.zoom
            mapViewModel.loadParkingSpotsInBounds(
                0.0,
                0.0,
                0.0,
                0.0,
                zoom,
                center = center
            )
        }
    }

    // Detect if dark mode is active (either from system or from settings)
    val isDarkMode = settingsState.darkMode || isSystemInDarkTheme()

    // Create dark mode map style matching Google Maps dark theme
    val mapStyleOptions = if (isDarkMode) {
        MapStyleOptions(
            """
            [
              {
                "elementType": "geometry",
                "stylers": [{"color": "#242f3e"}]
              },
              {
                "elementType": "labels.text.stroke",
                "stylers": [{"color": "#242f3e"}]
              },
              {
                "elementType": "labels.text.fill",
                "stylers": [{"color": "#d1d1d1"}]
              },
              {
                "featureType": "administrative.locality",
                "elementType": "labels.text.fill",
                "stylers": [{"color": "#d1d1d1"}]
              },
              {
                "featureType": "poi",
                "elementType": "labels.text.fill",
                "stylers": [{"color": "#d1d1d1"}]
              },
              {
                "featureType": "poi.park",
                "elementType": "geometry",
                "stylers": [{"color": "#263c3f"}]
              },
              {
                "featureType": "poi.park",
                "elementType": "labels.text.fill",
                "stylers": [{"color": "#d1d1d1"}]
              },
              {
                "featureType": "road",
                "elementType": "geometry",
                "stylers": [{"color": "#5C748D"}]
              },
              {
                "featureType": "road",
                "elementType": "geometry.stroke",
                "stylers": [{"color": "#4a5f75"}]
              },
              {
                "featureType": "road",
                "elementType": "labels.text.fill",
                "stylers": [{"color": "#d1d1d1"}]
              },
              {
                "featureType": "road.highway",
                "elementType": "geometry",
                "stylers": [{"color": "#6b83a0"}]
              },
              {
                "featureType": "road.highway",
                "elementType": "geometry.stroke",
                "stylers": [{"color": "#556b85"}]
              },
              {
                "featureType": "road.highway",
                "elementType": "labels.text.fill",
                "stylers": [{"color": "#d1d1d1"}]
              },
              {
                "featureType": "transit",
                "elementType": "geometry",
                "stylers": [{"color": "#2f3948"}]
              },
              {
                "featureType": "transit.station",
                "elementType": "labels.text.fill",
                "stylers": [{"color": "#d1d1d1"}]
              },
              {
                "featureType": "water",
                "elementType": "geometry",
                "stylers": [{"color": "#17263c"}]
              },
              {
                "featureType": "water",
                "elementType": "labels.text.fill",
                "stylers": [{"color": "#d1d1d1"}]
              },
              {
                "featureType": "water",
                "elementType": "labels.text.stroke",
                "stylers": [{"color": "#17263c"}]
              }
            ]
        """.trimIndent()
        )
    } else {
        null // Use default light map style
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                mapType = MapType.NORMAL,
                isTrafficEnabled = settingsState.showTraffic,
                isMyLocationEnabled = hasLocationPermission, // blue dot
                mapStyleOptions = mapStyleOptions
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = false,  // Using custom button instead
            ),
            onMapLongClick = onMapLongClick
        ) {
            // Render the markers passed from MapScreen
            content()
        }

        // Custom "My Location" button
        if (settingsState.showMyLocationButton && hasLocationPermission) {
            val coroutineScope = rememberCoroutineScope()

            FloatingActionButton(
                onClick = {
                    userLocation?.let { location ->
                        // Reset the manual movement flag to re-enable auto-following
                        // This allows user to resume tracking after manually exploring the map
                        hasUserMovedCamera = false

                        // Animate camera to user location (uses animation unlike auto-center for UX feedback)
                        coroutineScope.launch {
                            isAutoCentering = true
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newLatLngZoom(
                                    location,
                                    15f
                                ),
                                durationMs = 700
                            )
                            isAutoCentering = false
                        }
                    }
                },
                containerColor = if (isDarkMode) cs.surface else Color.White,
                contentColor = if (isDarkMode) cs.onSurface else Color(0xFF161616),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 10.dp, vertical = 34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "My Location"
                )
            }
        }
    }
}