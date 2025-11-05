package com.example.motorcycleparking.domain.models

import com.google.firebase.firestore.GeoPoint

data class ParkingSpot(
    val name: String,
    val id: String,
    val note: String,
    val displayAddress: String,
    val geoPoint: GeoPoint,
    val geoHash: String,
    val paid: Boolean,
)