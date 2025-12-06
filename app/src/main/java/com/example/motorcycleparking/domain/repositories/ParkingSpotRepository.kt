package com.example.motorcycleparking.domain.repositories

import com.example.motorcycleparking.domain.models.ParkingSpot
import com.google.android.gms.maps.model.LatLng

interface ParkingSpotRepository {
    suspend fun getParkingSpotsInBounds(
        swLat: Double,
        swLng: Double,
        neLat: Double,
        neLng: Double,
        zoom: Float,
        position: LatLng
    ): Result<List<ParkingSpot>>

    /**
     * Adds a new user-contributed parking spot to the database.
     *
     * @param name Display name for the parking spot
     * @param note Additional user notes or description
     * @param address Human-readable address
     * @param latitude Coordinate latitude
     * @param longitude Coordinate longitude
     * @param isFree Whether the parking is free or paid
     */
    suspend fun addParkingSpot(
        name: String,
        note: String,
        displayAddress: String,
        latitude: Double,
        longitude: Double,
        isFree: Boolean
    ): Result<String>
}