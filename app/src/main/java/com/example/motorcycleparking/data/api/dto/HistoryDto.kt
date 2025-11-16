package com.example.motorcycleparking.data.api.dto

import com.google.firebase.Timestamp

data class HistoryDto(
    val id: String,
    val timestamp: Timestamp,
) {
    constructor() : this("", Timestamp.now())
}
