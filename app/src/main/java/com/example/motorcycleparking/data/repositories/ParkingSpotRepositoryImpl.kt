package com.example.motorcycleparking.data.repositories

import com.example.motorcycleparking.data.api.FirebaseRepo
import com.example.motorcycleparking.domain.models.ParkingSpot
import com.example.motorcycleparking.domain.repositories.ParkingSpotRepository
import com.google.firebase.firestore.GeoPoint
import javax.inject.Inject

class ParkingSpotRepositoryImpl @Inject constructor(
    private val firebaseRepo: FirebaseRepo
): ParkingSpotRepository {

    override suspend fun getAllParkingSpots(): Result<List<ParkingSpot>> {

        return firebaseRepo.getParkingspotsNearYou(geoPoint = GeoPoint(0.0, 0.0), 0)
    }
}