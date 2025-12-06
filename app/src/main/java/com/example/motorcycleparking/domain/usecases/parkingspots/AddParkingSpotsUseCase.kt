package com.example.motorcycleparking.domain.usecases.parkingspots

import com.example.motorcycleparking.domain.repositories.ParkingSpotRepository
import javax.inject.Inject

class AddParkingSpotsUseCase @Inject constructor(
    private val repository: ParkingSpotRepository
) {

    companion object {
        private const val TAG = "AddParkingSpotUseCase"
    }
}