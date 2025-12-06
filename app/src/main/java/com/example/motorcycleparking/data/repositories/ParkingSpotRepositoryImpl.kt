package com.example.motorcycleparking.data.repositories

import com.example.motorcycleparking.data.api.FirebaseRepo
import com.example.motorcycleparking.data.api.dto.ParkingSpotDto
import com.example.motorcycleparking.domain.models.ParkingSpot
import com.example.motorcycleparking.domain.repositories.ParkingSpotRepository
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class ParkingSpotRepositoryImpl @Inject constructor(
    private val firebaseRepo: FirebaseRepo
) : ParkingSpotRepository {
    override suspend fun getParkingSpotsInBounds(
        swLat: Double,
        swLng: Double,
        neLat: Double,
        neLng: Double,
        zoom: Float,
        position: LatLng
    ): Result<List<ParkingSpot>> {
        return try {
            val dtoResult =
                firebaseRepo.getParkingSpotsInBounds(zoom, position)

            dtoResult.map { dtoList ->
                dtoList.map { it.toDomain() }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addParkingSpot(
        name: String,
        note: String,
        displayAddress: String,
        latitude: Double,
        longitude: Double,
        isFree: Boolean
    ): Result<String> {
        return firebaseRepo.addParkingSpot(
            name = name,
            note = note,
            displayAddress = displayAddress,
            latitude = latitude,
            longitude = longitude,
            isFree = isFree
        )
    }

    override suspend fun getParkingById(spotId: String): Result<ParkingSpot> {
        TODO("Not yet implemented")
    }
}

fun ParkingSpotDto.toDomain(): ParkingSpot {
    return ParkingSpot(
        name = this.name,
        id = this.id,
        note = this.note,
        displayAddress = this.displayAddress,
        latitude = this.geoPoint?.latitude,
        longitude = this.geoPoint?.longitude,
        free = this.free
    )
}
