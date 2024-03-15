package com.example.mapsapp.model

import com.google.android.gms.maps.model.LatLng

data class MarkerInfo(val name: String, val coordinates: LatLng, val type: String)