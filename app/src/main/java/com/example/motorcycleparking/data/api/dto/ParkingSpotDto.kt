package com.example.motorcycleparking.data.api.dto

import com.google.firebase.firestore.GeoPoint

data class ParkingSpotDto(
    val name: String = "",
    val nameLowercase: String = "",
    val id: String = "",
    val note: String = "",
    val displayAddress: String = "",
    val geoPoint: GeoPoint = GeoPoint(0.0, 0.0),
    val free: Boolean = false,
) {
    constructor() : this("", "", "", "", "", GeoPoint(0.0, 0.0), false)
}

