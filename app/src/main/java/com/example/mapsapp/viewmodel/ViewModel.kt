package com.example.mapsapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mapsapp.model.MarkerInfo
import com.google.android.gms.maps.model.LatLng

class ViewModel: ViewModel() {

    // AQUI SE TIENEN QUE PONER COSAS QUE ESTAN EN OTRAS SCREENS

    private var _marker = MutableLiveData(MarkerInfo("ITB", LatLng(41.4534265, 2.1837151), "itb", null))
    var marker = _marker

    private var _markers = MutableLiveData<MutableList<MarkerInfo>>()
    var markers = _markers



    private val _cameraPermissionGrented = MutableLiveData(false)
    val cameraPermissionGrented = _cameraPermissionGrented

    private val _shouldShowPermissionRationale = MutableLiveData(false)
    val shouldShowPermissionRationale = _shouldShowPermissionRationale

    private val _showPermissionDenied = MutableLiveData(false)
    val showPermissionDenied = _showPermissionDenied

    fun setCameraPermissionGranted(granted: Boolean) {
        _cameraPermissionGrented.value = granted
    }

    fun setShouldShowPermissionRationale(should: Boolean) {
        _shouldShowPermissionRationale.value = should
    }

    fun setShowPermissionDenied(denied: Boolean) {
        _showPermissionDenied.value = denied
    }
}