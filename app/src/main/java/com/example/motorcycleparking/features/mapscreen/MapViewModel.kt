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
import com.example.motorcycleparking.domain.usecases.GetAllParkingSpotsUseCase
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
import androidx.compose.runtime.getValue

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getAllParkingSpotsUseCase: GetAllParkingSpotsUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "MapViewModel"
    }

    private val _uiState = MutableStateFlow<MapUiState>(MapUiState(isLoading = true))
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private val _userLocation = mutableStateOf<LatLng?>(null)
    val userLocation: State<LatLng?> = _userLocation
    private var locationCallback: LocationCallback? = null

    init {
        loadParkingSpots()
    }

    private fun loadParkingSpots() {
        viewModelScope.launch {
            getAllParkingSpotsUseCase()
                .onSuccess { parkingSpots ->
                    Log.d(TAG, "Received ${parkingSpots.size} parking spots from firestore")

                    _uiState.value = MapUiState(
                        isLoading = false,
                        spots = parkingSpots
                    )
                }
                .onFailure { exception ->
                    Log.e(TAG, "Error loading parking spots from firestore", exception)
                    _uiState.value = MapUiState(
                        isLoading = false,
                        errorMessage = exception.message ?: "Unknown error"
                    )
                }
        }
    }


    // Function to fetch the user's location and update the state
    fun fetchUserLocation(context: Context, fusedLocationClient: FusedLocationProviderClient) {
        if (ContextCompat.checkSelfPermission( // Check if the location permission is granted
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                // Fetch the last known location
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        // Update the user's location in the state
                        val userLatLng = LatLng(it.latitude, it.longitude)
                        _userLocation.value = userLatLng
                    }
                }
            } catch (e: SecurityException) {
                Timber.e("Permission for location access was revoked: ${e.localizedMessage}")
            }
        } else {
            Timber.e("Location permission is not granted.")
        }
    }

    fun startLocationUpdates(context: Context, fusedLocationClient: FusedLocationProviderClient){
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_DENIED
        ) {
            return
        }
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 10000L // Update every 10 seconds.
        ).apply{
            setMinUpdateIntervalMillis(5000L) // Fastest update every 5 seconds
        }.build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    _userLocation.value = LatLng(location.latitude, location.longitude)
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Timber.e("Error requesting location updates: ${e.localizedMessage}")
        }
    }

    fun stopLocationUpdates(fusedLocationClient: FusedLocationProviderClient) {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
    }

    override fun onCleared() {
        super.onCleared()
        locationCallback = null
    }
}
