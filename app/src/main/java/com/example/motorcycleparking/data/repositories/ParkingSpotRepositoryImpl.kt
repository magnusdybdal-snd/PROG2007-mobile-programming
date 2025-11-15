package com.example.motorcycleparking.data.repositories

import com.example.motorcycleparking.data.api.FirebaseRepo
import com.example.motorcycleparking.domain.models.ParkingSpot
import com.example.motorcycleparking.domain.repositories.ParkingSpotRepository
import com.google.firebase.firestore.GeoPoint
import javax.inject.Inject

class ParkingSpotRepositoryImpl @Inject constructor(
    private val firebaseRepo: FirebaseRepo
): ParkingSpotRepository {

    override suspend fun getNearbyParkingSpots(
        geoPoint: GeoPoint,
        precision: Int
    ): Result<List<ParkingSpot>> {
        return firebaseRepo.getParkingspotsNearYou(geoPoint = geoPoint, precision = precision)
    }

    override suspend fun getParkingSpotsInBounds(
        swLat: Double,
        swLng: Double,
        neLat: Double,
        neLng: Double
    ): Result<List<ParkingSpot>> {
        return firebaseRepo.getParkingSpotsInBounds(swLat, swLng, neLat, neLng)
    }

}