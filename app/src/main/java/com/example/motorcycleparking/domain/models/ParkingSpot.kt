package com.example.motorcycleparking.domain.models

import com.google.firebase.firestore.GeoPoint

data class ParkingSpot(
    val name: String? = null,
    val id: String? = null,
    val note: String? = null,
    val displayAddress: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val free: Boolean? = null
)