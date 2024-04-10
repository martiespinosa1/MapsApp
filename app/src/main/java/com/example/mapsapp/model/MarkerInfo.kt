package com.example.mapsapp.model

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng
import java.util.UUID

data class MarkerInfo(
    val name: String,
    val coordinates: LatLng,
    val type: String,
    val photos: MutableList<Bitmap>?,
    val userId: String?,
    val markerId: String = UUID.randomUUID().toString()
)