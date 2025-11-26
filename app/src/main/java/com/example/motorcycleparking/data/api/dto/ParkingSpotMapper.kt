package com.example.motorcycleparking.data.api.dto

import com.example.motorcycleparking.domain.models.ParkingSpot

fun ParkingSpotDto.toDomain(): ParkingSpot {
    return ParkingSpot(
        id = this.id,
        name = this.name,
        note = this.note,
        displayAddress = this.displayAddress,
        latitude = this.geoPoint.latitude,
        longitude = this.geoPoint.longitude,
        free = this.free
    )
}

fun List<ParkingSpotDto>.toDomain(): List<ParkingSpot> {
    return this.map { it.toDomain() }
}