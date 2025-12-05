package com.example.motorcycleparking.domain.usecases

import com.example.motorcycleparking.domain.models.ParkingSpot
import com.example.motorcycleparking.domain.repositories.ParkingSpotRepository
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class GetNearbyParkingSpotsUseCase @Inject constructor(
    private val repository: ParkingSpotRepository
) {
    suspend fun getInBounds(
        swLat: Double,
        swLng: Double,
        neLat: Double,
        neLng: Double,
        zoom: Float,
        center: LatLng
    ): Result<List<ParkingSpot>> {
        return repository.getParkingSpotsInBounds(
            swLat = swLat,
            swLng = swLng,
            neLat = neLat,
            neLng = neLng,
            zoom = zoom,
            position = center
        )
    }
}