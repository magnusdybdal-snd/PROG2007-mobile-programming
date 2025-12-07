package com.example.motorcycleparking.domain.models

data class SearchHistoryItem(
    val parkingSpotId: String,
    val searchQuery: String,
    val timestampMills: Long // Timestamp in milliseconds to keep seperate from Firebase SDK
)