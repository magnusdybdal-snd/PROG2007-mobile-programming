package com.example.motorcycleparking.domain.models

import com.google.firebase.firestore.GeoPoint

data class ParkingSpot(
    val name: String,
    val id: String,
    val note: String,
    val displayAddress: String,
    val latitude: Double,
    val longitude: Double,
    val free: Boolean
)