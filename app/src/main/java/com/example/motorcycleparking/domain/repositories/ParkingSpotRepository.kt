package com.example.motorcycleparking.domain.repositories

import com.example.motorcycleparking.domain.models.ParkingSpot

interface ParkingSpotRepository {
    suspend fun getAllParkingSpots(): Result<List<ParkingSpot>>
}