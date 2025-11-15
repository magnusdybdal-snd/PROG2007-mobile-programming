package com.example.motorcycleparking.features.mapscreen

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.motorcycleparking.core.utils.GeoUtils
import com.example.motorcycleparking.domain.usecases.GetNearbyParkingSpotsUseCase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getNearbyParkingSpotsUseCase: GetNearbyParkingSpotsUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "MapViewModel"
    }

    private val _uiState = MutableStateFlow<MapUiState>(MapUiState(isLoading = true))
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private val _userLocation = mutableStateOf<LatLng?>(null)
    val userLocation: State<LatLng?> = _userLocation
    private var locationCallback: LocationCallback? = null

    private var lastLoadedLocation: LatLng? = null
    private var lastZoomLevel: Float? = null
    private val loadThreshold = 200.0 // meters - only reload if camera moved this far

    init {
        Log.d(TAG, "🚀 MapViewModel initialized")
    }

    // 🎯 NEW: Load parking spots based on camera position
    fun loadParkingSpotsForCamera(cameraPosition: LatLng, zoomLevel: Float) {
        if (shouldReload(cameraPosition)) {
            val precision = GeoUtils.zoomLevelToPrecision(zoomLevel)
            val estimatedRadius = GeoUtils.precisionToRadius(precision)

            Log.d(TAG, "📍 Loading parking spots:")
            Log.d(TAG, "   Position: (${cameraPosition.latitude}, ${cameraPosition.longitude})")
            Log.d(TAG, "   Zoom: $zoomLevel → Precision: $precision")
            Log.d(TAG, "   Estimated radius: ${estimatedRadius}m")

            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val geoPoint = GeoPoint(cameraPosition.latitude, cameraPosition.longitude)

                getNearbyParkingSpotsUseCase(geoPoint, precision)
                    .onSuccess { parkingSpots ->
                        Log.d(TAG, "✅ SUCCESS! Received ${parkingSpots.size} parking spots for camera view")

                        if (parkingSpots.isNotEmpty()) {
                            parkingSpots.forEachIndexed { index, spot ->
                                Log.d(TAG, "  📍 Spot ${index + 1}: ${spot.name} at (${spot.latitude}, ${spot.longitude})")
                            }
                        } else {
                            Log.d(TAG, "  ⚠️ No parking spots found in this area")
                        }

                        _uiState.value = MapUiState(
                            isLoading = false,
                            spots = parkingSpots
                        )
                        lastLoadedLocation = cameraPosition
                        lastZoomLevel = zoomLevel
                    }
                    .onFailure { exception ->
                        Log.e(TAG, "❌ ERROR loading parking spots: ${exception.message}", exception)
                        _uiState.value = MapUiState(
                            isLoading = false,
                            errorMessage = exception.message ?: "Unknown error"
                        )
                    }
            }
        }
    }

    private fun shouldReload(newLocation: LatLng): Boolean {
        val last = lastLoadedLocation ?: return true
        val distance = FloatArray(1)
        Location.distanceBetween(
            last.latitude, last.longitude,
            newLocation.latitude, newLocation.longitude,
            distance
        )
        val shouldReload = distance[0] > loadThreshold
        if (!shouldReload) {
            Log.d(TAG, "⏭️ Skipping reload - camera only moved ${distance[0]}m")
        }
        return shouldReload
    }
    private fun shouldReloadForZoom(newZoom: Float): Boolean {
        val lastZoom = lastZoomLevel ?: return true

        // Calculate precision for old and new zoom
        val oldPrecision = GeoUtils.zoomLevelToPrecision(lastZoom)
        val newPrecision = GeoUtils.zoomLevelToPrecision(newZoom)

        // Reload if precision changed (crossed a zoom threshold)
        val shouldReload = oldPrecision != newPrecision

        if (shouldReload) {
            Log.d(TAG, "🔍 Zoom changed: $lastZoom → $newZoom (precision: $oldPrecision → $newPrecision)")
        }

        return shouldReload
    }

    fun fetchUserLocation(context: Context, fusedLocationClient: FusedLocationProviderClient) {
        Log.d(TAG, "🔍 fetchUserLocation called")

        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "✅ Location permission granted")
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val userLatLng = LatLng(location.latitude, location.longitude)
                        _userLocation.value = userLatLng
                        Log.d(TAG, "📍 User location found: (${location.latitude}, ${location.longitude})")

                        // // Initial load with default zoom
                        loadParkingSpotsForCamera(userLatLng, 15f)
                    } else {
                        Log.w(TAG, "⚠️ Location is null - GPS might not be ready yet")
                    }
                }.addOnFailureListener { exception ->
                    Log.e(TAG, "❌ Failed to get location: ${exception.message}", exception)
                }
            } catch (e: SecurityException) {
                Log.e(TAG, "❌ SecurityException: ${e.localizedMessage}", e)
            }
        } else {
            Log.e(TAG, "❌ Location permission NOT granted")
        }
    }

    fun startLocationUpdates(context: Context, fusedLocationClient: FusedLocationProviderClient) {
        Log.d(TAG, "🔄 startLocationUpdates called")

        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_DENIED
        ) {
            Log.e(TAG, "❌ Cannot start location updates - permission denied")
            return
        }

        Log.d(TAG, "✅ Setting up location updates...")

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 10000L
        ).apply {
            setMinUpdateIntervalMillis(5000L)
        }.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    val newLocation = LatLng(location.latitude, location.longitude)
                    Log.d(TAG, "📍 Location update: (${location.latitude}, ${location.longitude})")
                    _userLocation.value = newLocation
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )
            Log.d(TAG, "✅ Location updates started successfully")
        } catch (e: SecurityException) {
            Log.e(TAG, "❌ Error requesting location updates: ${e.localizedMessage}", e)
            Timber.e("Error requesting location updates: ${e.localizedMessage}")
        }
    }

    fun stopLocationUpdates(fusedLocationClient: FusedLocationProviderClient) {
        Log.d(TAG, "⏹️ Stopping location updates")
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
            Log.d(TAG, "✅ Location updates stopped")
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "🧹 MapViewModel cleared")
        locationCallback = null
    }
}