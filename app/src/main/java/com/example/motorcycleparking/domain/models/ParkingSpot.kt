package com.example.motorcycleparking.domain.models

import com.google.firebase.firestore.GeoPoint

data class ParkingSpot(
    val name: String?,
    val id: String?,
    val note: String?,
    val displayAddress: String? = null,
    val geoPoint: GeoPoint? = null,
    val free: Boolean? = null,
    val image: Metadata? = null // TODO: Metadata correct?
)