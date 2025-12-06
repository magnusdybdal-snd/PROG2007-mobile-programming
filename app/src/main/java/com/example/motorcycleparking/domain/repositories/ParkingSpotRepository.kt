package com.example.motorcycleparking.domain.repositories

import com.example.motorcycleparking.domain.models.ParkingSpot
import com.google.android.gms.maps.model.LatLng

interface ParkingSpotRepository {
    suspend fun getParkingSpotsInBounds(
        swLat: Double,
        swLng: Double,
        neLat: Double,
        neLng: Double,
        zoom: Float,
        position: LatLng
    ): Result<List<ParkingSpot>>

    suspend fun addParkingSpot(
        name: String,
        note: String,
        displayAddress: String,
        latitude: Double,
        longitude: Double,
        isFree: Boolean
    ): Result<Unit>
}