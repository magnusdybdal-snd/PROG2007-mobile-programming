package com.example.motorcycleparking.domain.usecases

import com.example.motorcycleparking.domain.models.ParkingSpot
import com.example.motorcycleparking.domain.repositories.ParkingSpotRepository
import javax.inject.Inject

class GetAllParkingSpotsUseCase @Inject constructor(
    private val repository: ParkingSpotRepository
) {
    suspend operator fun invoke(): Result<List<ParkingSpot>> {
        // TODO: UNCOMMENT WHEN IMPLEMENTED
        // return repository.getAllParkingSpots()

        // Temporary mock data
        return Result.success(
            listOf(
                ParkingSpot(
                    id = "1",
                    name = "Downtown Parking",
                    note = "Near city center",
                    displayAddress = "123 Main St",
                    latitude = 63.4305,
                    longitude = 10.3951,
                    free = true
                ),
                ParkingSpot(
                    id = "2",
                    name = "Harbor Parking",
                    note = "Waterfront area",
                    displayAddress = "456 Harbor Rd",
                    latitude = 63.4365,
                    longitude = 10.3989,
                    free = false
                )
            )
        )
    }
}