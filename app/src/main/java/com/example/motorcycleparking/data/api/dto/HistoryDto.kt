package com.example.motorcycleparking.data.api.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class HistoryDto(
    @get:PropertyName("parkingspot_id") @set:PropertyName("parkingspot_id")
    var parkingId: String,
    var search: String,
    var timestamp: Timestamp,
) {
    constructor() : this("", "", Timestamp.now())
}
