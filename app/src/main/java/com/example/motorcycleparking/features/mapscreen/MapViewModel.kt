package com.example.motorcycleparking.features.mapscreen

import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.motorcycleparking.domain.usecases.GetNearbyParkingSpotsUseCase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
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

    private val _uiState = MutableStateFlow(MapUiState(isLoading = true))
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private val _userLocation = mutableStateOf<LatLng?>(null)
    val userLocation: State<LatLng?> = _userLocation
    private var locationCallback: LocationCallback? = null

    init {
        Log.d(TAG, "MapViewModel initialized")
    }

    // Load parking spots based on camera position
    fun loadParkingSpotsInBounds(
        swLat: Double,
        swLng: Double,
        neLat: Double,
        neLng: Double,
        zoom: Float,
        center: LatLng
    ) {
        Log.d(TAG, "📍 Loading spots for visible bounds")
        Log.d(TAG, "   SW: ($swLat, $swLng)")
        Log.d(TAG, "   NE: ($neLat, $neLng)")

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            getNearbyParkingSpotsUseCase.getInBounds(
                swLat = swLat,
                swLng = swLng,
                neLat = neLat,
                neLng = neLng,
                zoom = zoom,
                center = center
            )
                .onSuccess { parkingSpots ->
                    Log.d(TAG, "✅ Found ${parkingSpots.size} spots in visible area")
                    parkingSpots.forEachIndexed { index, spot ->
                        Log.d(TAG, "   📍 ${index + 1}. ${spot.name}")
                    }

                    _uiState.value = MapUiState(
                        isLoading = false,
                        spots = parkingSpots
                    )
                }
                .onFailure { exception ->
                    Log.e(TAG, "❌ Error: ${exception.message}", exception)
                    _uiState.value = MapUiState(
                        isLoading = false,
                        errorMessage = exception.message ?: "Unknown error"
                    )
                }
        }
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
                        val userLatLng = com.google.android.gms.maps.model.LatLng(
                            location.latitude,
                            location.longitude
                        )
                        _userLocation.value = userLatLng
                        Log.d(
                            TAG,
                            "📍 User location found: (${location.latitude}, ${location.longitude})"
                        )

                        // Don't load spots here - let the map camera do it when it's ready
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
                    val newLocation = com.google.android.gms.maps.model.LatLng(
                        location.latitude,
                        location.longitude
                    )
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