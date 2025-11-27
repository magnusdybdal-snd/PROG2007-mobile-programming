package com.example.motorcycleparking.core.core_ui.composable

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.motorcycleparking.features.mapscreen.MapViewModel
import com.example.motorcycleparking.features.settingsscreen.SettingsViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import timber.log.Timber

@Composable
fun GoogleMapView(
    mapViewModel: MapViewModel,
    settingsViewModel: SettingsViewModel,
    cameraPositionState: com.google.maps.android.compose.CameraPositionState = rememberCameraPositionState(),
    onMapLongClick: (com.google.android.gms.maps.model.LatLng) -> Unit = {},
    content: @Composable () -> Unit  // Changed from 'function' to 'content' (better naming)
) {
    // Obtain the current context
    val context = LocalContext.current
    // Observe the user's location from the ViewModel
    val userLocation by mapViewModel.userLocation
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

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
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Request the location permission when the composable is launched
    LaunchedEffect(Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            // Check if the location permission is already granted
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
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
                permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
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

    // Update camera position when user location changes and auto-center is enabled
    LaunchedEffect(userLocation, settingsState.autoCenterOnLocation) {
        if (settingsState.autoCenterOnLocation) {
            userLocation?.let {
                cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
            }
        }
    }

    // Load ALL spots visible on screen when camera stops moving
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            val projection = cameraPositionState.projection

            if (projection != null) {
                // Get the actual visible region bounds via "google magic"
                // Converts screen pixels to lagLng.
                val visibleRegion = projection.visibleRegion
                val bounds = visibleRegion.latLngBounds

                Timber.d("📍 Camera stopped - visible area:")
                Timber.d("   SW: (${bounds.southwest.latitude}, ${bounds.southwest.longitude})")
                Timber.d("   NE: (${bounds.northeast.latitude}, ${bounds.northeast.longitude})")

                // Load all spots in this bounding box
                mapViewModel.loadParkingSpotsInBounds(
                    swLat = bounds.southwest.latitude,
                    swLng = bounds.southwest.longitude,
                    neLat = bounds.northeast.latitude,
                    neLng = bounds.northeast.longitude
                )
            }
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            mapType = MapType.NORMAL,
            isTrafficEnabled = settingsState.showTraffic,
            isMyLocationEnabled = hasLocationPermission // blue dot
        ),
        uiSettings = MapUiSettings(
            myLocationButtonEnabled = settingsState.showMyLocationButton,
        ),
        onMapLongClick = onMapLongClick
    ) {
        // Render the markers passed from MapScreen
        content()
    }
}