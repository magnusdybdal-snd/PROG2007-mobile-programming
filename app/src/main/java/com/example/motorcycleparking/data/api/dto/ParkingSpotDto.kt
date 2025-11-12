package com.example.motorcycleparking.data.api.dto

import com.google.firebase.firestore.GeoPoint

data class ParkingSpotDto(
    val id: String = "",
    val name: String = "",
    val note: String = "",
    val displayAddress: String = "",
    val geoPoint: GeoPoint = GeoPoint(0.0, 0.0),
    val geoHash: String = "",
    val free: Boolean = true
) {
    constructor() : this("", "", "", "", GeoPoint(0.0, 0.0),"",  false)
}

