package com.example.mapsapp.model

import com.google.android.gms.maps.model.LatLng
import java.util.UUID

data class MarkerInfo(
    val name: String,
    val coordinates: LatLng,
    val type: String,
    val photos: MutableList<String>?,
    val userId: String?,
    var markerId: String = UUID.randomUUID().toString()
)