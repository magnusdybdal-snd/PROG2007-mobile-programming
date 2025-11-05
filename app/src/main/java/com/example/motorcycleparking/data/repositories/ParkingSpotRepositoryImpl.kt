package com.example.motorcycleparking.data.repositories

import com.example.motorcycleparking.data.api.FirebaseRepo
import com.example.motorcycleparking.domain.repositories.ParkingSpotRepository
import javax.inject.Inject

class ParkingSpotRepositoryImpl @Inject constructor(
    private val firebaseRepo: FirebaseRepo
): ParkingSpotRepository {
    //TODO: Implement
}