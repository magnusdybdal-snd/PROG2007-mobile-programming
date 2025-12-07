package com.example.motorcycleparking.domain.models

/**
 * Domain model representing a search result on a parking spot.
 * This is used when the user performs a live search
 */
data class SearchResult(
    val spotId: String,
    val name: String
)