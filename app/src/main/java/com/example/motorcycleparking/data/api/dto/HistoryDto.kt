package com.example.motorcycleparking.data.api.dto

import com.google.firebase.Timestamp

data class HistoryDto(
    var parkingspot_id: String,
    var search: String,
    var timestamp: Timestamp,
) {
    constructor() : this("", "", Timestamp.now())
}
