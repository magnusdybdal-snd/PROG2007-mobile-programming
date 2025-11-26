package com.example.motorcycleparking.domain.usecases

import com.example.motorcycleparking.domain.models.ParkingSpot
import com.example.motorcycleparking.domain.repositories.ParkingSpotRepository
import com.google.firebase.firestore.GeoPoint
import javax.inject.Inject

class GetNearbyParkingSpotsUseCase @Inject constructor(
    private val repository: ParkingSpotRepository
) {
    suspend operator fun invoke(
        geoPoint: GeoPoint,
        precision: Int = 6
    ): Result<List<ParkingSpot>> {
        return repository.getNearbyParkingSpots(geoPoint, precision)
    }

    suspend fun getInBounds(
        swLat: Double,
        swLng: Double,
        neLat: Double,
        neLng: Double
    ): Result<List<ParkingSpot>> {
        return repository.getParkingSpotsInBounds(
            swLat = swLat,
            swLng = swLng,
            neLat = neLat,
            neLng = neLng
        )
    }
}