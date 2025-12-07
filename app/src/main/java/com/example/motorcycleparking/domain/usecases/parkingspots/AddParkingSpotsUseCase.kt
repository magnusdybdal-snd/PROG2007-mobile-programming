package com.example.motorcycleparking.domain.usecases.parkingspots

import android.util.Log
import com.example.motorcycleparking.domain.repositories.ParkingSpotRepository
import javax.inject.Inject

class AddParkingSpotsUseCase @Inject constructor(
    private val repository: ParkingSpotRepository
) {

    companion object {
        private const val TAG = "AddParkingSpotUseCase"
    }

    suspend operator fun invoke(
        name: String,
        note: String,
        displayAddress: String,
        latitude: Double,
        longitude: Double,
        isFree: Boolean
    ): Result<String> {
        return repository.addParkingSpot(
            name = name,
            note = note,
            displayAddress = displayAddress,
            latitude = latitude,
            longitude = longitude,
            isFree = isFree
        )
    }
}