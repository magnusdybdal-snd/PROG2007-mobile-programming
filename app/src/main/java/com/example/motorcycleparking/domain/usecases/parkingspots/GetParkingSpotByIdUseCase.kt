package com.example.motorcycleparking.domain.usecases.parkingspots

import com.example.motorcycleparking.domain.models.ParkingSpot
import com.example.motorcycleparking.domain.repositories.ParkingSpotRepository
import javax.inject.Inject

/**
 * Use case for retrieving a single parking spot by its unique ID
 */
class GetParkingSpotByIdUseCase @Inject constructor(
    private val repository: ParkingSpotRepository
) {
    /**
     * Fetches a parking spot by its unique ID
     *
     * @param spotId the unique ID of the parking spot
     * @return Result with the parking spot or an exception if not found
     */
    suspend operator fun invoke(spotId: String): Result<ParkingSpot> {
        return repository.getParkingById(spotId)
    }
}