package com.example.motorcycleparking.data.api.dto

data class FavoritesDto(
    val ids: List<String>
) {
    constructor() : this(emptyList())
}
