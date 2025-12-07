package com.example.motorcycleparking.domain.repositories

import com.example.motorcycleparking.domain.models.ParkingSpot
import com.google.android.gms.maps.model.LatLng

/**
 * Repository for managing parking spots
 */
interface ParkingSpotRepository {

    /**
     * Retrieves all parking spots in bounds
     *
     * @param swLat
     * @param swLng
     * @param neLat
     * @param neLng
     * @param zoom
     * @param position
     * @return Result with list of parking spots, or an exception
     */
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
     * @param displayAddress Human-readable address
     * @param latitude Coordinate latitude
     * @param longitude Coordinate longitude
     * @param isFree Whether the parking is free or paid
     * @return Result with docId of parking spot
     */
    suspend fun addParkingSpot(
        name: String,
        note: String,
        displayAddress: String,
        latitude: Double,
        longitude: Double,
        isFree: Boolean
    ): Result<String>

    suspend fun getParkingById(spotId: String): Result<ParkingSpot>
}