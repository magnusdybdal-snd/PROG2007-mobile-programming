package com.example.motorcycleparking.data.api.dto

data class FavoritesDto(
    val favorites: List<String> = emptyList()
) {
    constructor() : this(emptyList())
}
