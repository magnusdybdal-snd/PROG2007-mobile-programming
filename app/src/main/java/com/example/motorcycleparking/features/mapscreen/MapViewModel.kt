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
import com.example.motorcycleparking.core.core_ui.composable.SearchResultItem
import com.example.motorcycleparking.data.api.FirebaseRepo
import com.example.motorcycleparking.domain.usecases.GetNearbyParkingSpotsUseCase
import com.example.motorcycleparking.domain.usecases.ToggleFavoriteUseCase
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
    private val getNearbyParkingSpotsUseCase: GetNearbyParkingSpotsUseCase,
    val firebaseRepo: FirebaseRepo,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "MapViewModel"
    }

    private val _uiState = MutableStateFlow(MapUiState(isLoading = true))
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private val _userLocation = mutableStateOf<LatLng?>(null)
    val userLocation: State<LatLng?> = _userLocation
    private var locationCallback: LocationCallback? = null

    // Track favorite parking spot IDs
    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    // Track search results (history + live search)
    private val _searchResults = MutableStateFlow<List<SearchResultItem>>(emptyList())
    val searchResults: StateFlow<List<SearchResultItem>> = _searchResults.asStateFlow()

    init {
        loadFavorites()
        Log.d(TAG, "MapViewModel initialized")
    }

    // Load parking spots using geohash around center location
    fun loadParkingSpots(center: LatLng, precision: Int = 6) {
        Log.d(TAG, "Loading spots near (${center.latitude}, ${center.longitude}) with precision $precision")

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val geoPoint = com.google.firebase.firestore.GeoPoint(center.latitude, center.longitude)

            getNearbyParkingSpotsUseCase(geoPoint, precision)
                .onSuccess { parkingSpots ->
                    Log.d(TAG, "Found ${parkingSpots.size} spots nearby")
                    parkingSpots.forEachIndexed { index, spot ->
                        Log.d(TAG, "   ${index + 1}. ${spot.name}")
                    }

                    _uiState.value = MapUiState(
                        isLoading = false,
                        spots = parkingSpots
                    )
                }
                .onFailure { exception ->
                    Log.e(TAG, "Error: ${exception.message}", exception)
                    _uiState.value = MapUiState(
                        isLoading = false,
                        errorMessage = exception.message ?: "Unknown error"
                    )
                }
        }
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
        Log.d(TAG, "Loading spots for visible bounds")
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
                    Log.d(TAG, "Found ${parkingSpots.size} spots in visible area")
                    parkingSpots.forEachIndexed { index, spot ->
                        Log.d(TAG, "   ${index + 1}. ${spot.name}")
                    }

                    _uiState.value = MapUiState(
                        isLoading = false,
                        spots = parkingSpots
                    )
                }
                .onFailure { exception ->
                    Log.e(TAG, "Error: ${exception.message}", exception)
                    _uiState.value = MapUiState(
                        isLoading = false,
                        errorMessage = exception.message ?: "Unknown error"
                    )
                }
        }
    }

    fun fetchUserLocation(context: Context, fusedLocationClient: FusedLocationProviderClient) {
        Log.d(TAG, "fetchUserLocation called")

        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "Location permission granted")
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
                            "User location found: (${location.latitude}, ${location.longitude})"
                        )

                        // Don't load spots here - let the map camera do it when it's ready
                    } else {
                        Log.w(TAG, "Location is null - GPS might not be ready yet")
                    }
                }.addOnFailureListener { exception ->
                    Log.e(TAG, "Failed to get location: ${exception.message}", exception)
                }
            } catch (e: SecurityException) {
                Log.e(TAG, "SecurityException: ${e.localizedMessage}", e)
            }
        } else {
            Log.e(TAG, "Location permission NOT granted")
        }
    }

    fun startLocationUpdates(context: Context, fusedLocationClient: FusedLocationProviderClient) {
        Log.d(TAG, "startLocationUpdates called")

        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_DENIED
        ) {
            Log.e(TAG, "Cannot start location updates - permission denied")
            return
        }

        Log.d(TAG, "Setting up location updates...")

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
                    Log.d(TAG, "Location update: (${location.latitude}, ${location.longitude})")
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
            Log.d(TAG, "Location updates started successfully")
        } catch (e: SecurityException) {
            Log.e(TAG, "Error requesting location updates: ${e.localizedMessage}", e)
            Timber.e("Error requesting location updates: ${e.localizedMessage}")
        }
    }

    fun stopLocationUpdates(fusedLocationClient: FusedLocationProviderClient) {
        Log.d(TAG, "Stopping location updates")
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
            Log.d(TAG, "Location updates stopped")
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "MapViewModel cleared")
        locationCallback = null
    }

    // Load user's favorite parking spots
    private fun loadFavorites() {
        viewModelScope.launch {
            try {
                firebaseRepo.getFavorites()
                    .onSuccess { favorites ->
                        _favoriteIds.value = favorites.mapNotNull { it.id }.toSet()
                        Log.d(TAG, "Loaded ${favorites.size} favorites")
                    }
                    .onFailure { e ->
                        Log.e(TAG, "Error loading favorites", e)
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading favorites", e)
            }
        }
    }

    // Toggle favorite status for a parking spot
    fun toggleFavorite(spotId: String) {
        viewModelScope.launch {
            toggleFavoriteUseCase(spotId)
                .onSuccess {
                    // Update local state
                    _favoriteIds.value = if (spotId in _favoriteIds.value) {
                        _favoriteIds.value - spotId
                    } else {
                        _favoriteIds.value + spotId
                    }
                    Log.d(TAG, "Toggled favorite for spot: $spotId")
                }
                .onFailure { e ->
                    Log.e(TAG, "Error toggling favorite", e)
                }
        }
    }

    // Check if a parking spot is favorited
    fun isFavorite(spotId: String): Boolean {
        return spotId in _favoriteIds.value
    }

    // Add a new parking spot
    fun addParkingSpot(
        name: String,
        note: String,
        address: String,
        latitude: Double,
        longitude: Double,
        isFree: Boolean,
        onSuccess: () -> Unit = {},
        onFailure: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            firebaseRepo.addParkingSpot(
                name = name,
                note = note,
                displayAddress = address,
                latitude = latitude,
                longitude = longitude,
                isFree = isFree
            )
                .onSuccess { spotId ->
                    Log.d(TAG, "Successfully added parking spot with ID: $spotId")
                    onSuccess()
                }
                .onFailure { exception ->
                    Log.e(TAG, "Failed to add parking spot: ${exception.message}", exception)
                    onFailure(exception.message ?: "Failed to add parking spot")
                }
        }
    }

    // Load search history when searchbar is expanded
    fun loadSearchHistory() {
        viewModelScope.launch {
            firebaseRepo.getSearchHistory()
                .onSuccess { historyList ->
                    // Fetch parking spot names for each history item
                    val historyItems = historyList.mapNotNull { historyDto ->
                        try {
                            val result = firebaseRepo.getParkingById(historyDto.parkingId)
                            result.getOrNull()?.let { parkingSpot ->
                                SearchResultItem(
                                    id = historyDto.parkingId,
                                    displayText = parkingSpot.name ?: "Unknown Location",
                                    isHistory = true
                                )
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error fetching parking spot for history item", e)
                            null
                        }
                    }
                    _searchResults.value = historyItems
                    Log.d(TAG, "Loaded ${historyItems.size} history items")
                }
                .onFailure { e ->
                    Log.e(TAG, "Error loading search history", e)
                    _searchResults.value = emptyList()
                }
        }
    }

    // Search for parking spots as user types
    fun searchParkingSpots(query: String) {
        if (query.isBlank()) {
            // Show history when query is empty
            loadSearchHistory()
            return
        }

        viewModelScope.launch {
            firebaseRepo.getTitlesInSearch(query)
                .onSuccess { searchDtoList ->
                    _searchResults.value = searchDtoList.map { searchDto ->
                        SearchResultItem(
                            id = searchDto.spotId,
                            displayText = searchDto.name,
                            isHistory = false
                        )
                    }
                    Log.d(TAG, "Found ${searchDtoList.size} search results for '$query'")
                }
                .onFailure { e ->
                    Log.e(TAG, "Error searching parking spots", e)
                    _searchResults.value = emptyList()
                }
        }
    }

    // Handle when user clicks on a search result
    fun onSearchResultClick(result: SearchResultItem, searchQuery: String) {
        viewModelScope.launch {
            try {
                // Save to search history if it's not already a history item
                if (!result.isHistory) {
                    firebaseRepo.addToSearchHistory(
                        spotId = result.id,
                        searched = searchQuery.ifBlank { result.displayText }
                    )
                    Log.d(TAG, "Added '${result.displayText}' to search history")
                }

                // Get the parking spot details
                firebaseRepo.getParkingById(result.id)
                    .onSuccess { parkingSpot ->
                        Log.d(TAG, "Found parking spot: ${parkingSpot.name}")
                        // The UI will handle showing the spot on the map
                    }
                    .onFailure { e ->
                        Log.e(TAG, "Error getting parking spot by ID", e)
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Error handling search result click", e)
            }
        }
    }
}