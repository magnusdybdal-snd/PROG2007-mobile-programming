package com.example.motorcycleparking.data.repositories

import com.example.motorcycleparking.data.FirebaseRepo
import com.example.motorcycleparking.domain.repositories.ParkingSpotRepository
import javax.inject.Inject

class ParkingSpotRepositoryImpl @Inject constructor(
    private val api: FirebaseRepo
): ParkingSpotRepository {
    //TODO: Implement
}