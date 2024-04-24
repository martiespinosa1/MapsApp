package com.example.mapsapp.model

import java.util.UUID

data class MarkerInfo(
    var name: String,
    var latitude: Double,
    var longitude: Double,
    val type: String,
    val photos: MutableList<String>?,
    var userId: String?,
    var markerId: String = UUID.randomUUID().toString()
) {
    constructor() : this("", Double.NaN, Double.NaN, "", null, null)
}
