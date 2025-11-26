package com.example.motorcycleparking.data.repositories

import com.example.motorcycleparking.data.api.FirebaseRepo
import com.example.motorcycleparking.data.api.dto.ParkingSpotDto
import com.example.motorcycleparking.domain.models.ParkingSpot
import com.example.motorcycleparking.domain.repositories.ParkingSpotRepository
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import javax.inject.Inject

class ParkingSpotRepositoryImpl @Inject constructor(
    private val firebaseRepo: FirebaseRepo
) : ParkingSpotRepository {

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
        neLng: Double,
        zoom: Float,
        position: LatLng
    ): Result<List<ParkingSpot>> {
        return try {
            val dtoResult =
                firebaseRepo.getParkingSpotsInBounds(swLat, swLng, neLat, neLng, zoom, position)

            dtoResult.map { dtoList ->
                dtoList.map { it.toDomain() }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
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
