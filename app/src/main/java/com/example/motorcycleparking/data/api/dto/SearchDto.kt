package com.example.motorcycleparking.data.api.dto

import com.google.firebase.firestore.DocumentId

data class SearchDto(
    @DocumentId
    var spotId: String,
    var name: String,
) {
    constructor() : this("", "")
}
