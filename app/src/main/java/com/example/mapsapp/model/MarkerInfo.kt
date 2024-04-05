package com.example.mapsapp.model

import android.graphics.Bitmap
import com.google.android.gms.maps.model.LatLng

data class MarkerInfo(val name: String, val coordinates: LatLng, val type: String, val fotos: MutableList<Bitmap>?)