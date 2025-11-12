package com.example.motorcycleparking.domain.repositories

import com.example.motorcycleparking.domain.models.ParkingSpot
import com.google.firebase.firestore.GeoPoint

interface ParkingSpotRepository {
    suspend fun getNearbyParkingSpots(
        geoPoint: GeoPoint,
        precision: Int = 6
    ): Result<List<ParkingSpot>>
}