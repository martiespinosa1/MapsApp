package com.example.mapsapp.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mapsapp.model.MarkerInfo
import com.google.android.gms.maps.model.LatLng

class ViewModel: ViewModel() {

    // AQUI SE TIENEN QUE PONER COSAS QUE ESTAN EN OTRAS SCREENS

    private var _marker = MutableLiveData(MarkerInfo("ITB", LatLng(41.4534265, 2.1837151), "itb", null))
    var marker = _marker

    private var _markers = MutableLiveData<MutableList<MarkerInfo>>()
    val markers = _markers

    private var _currentMarker: MarkerInfo = MarkerInfo("ITB", LatLng(41.4534265, 2.1837151), "itb", null)
    var currentMarker = _currentMarker

    private val _fotos = MutableLiveData<MutableList<Bitmap>>(mutableListOf())
    val fotos: LiveData<MutableList<Bitmap>> = _fotos


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

    fun addPhoto(photo: Bitmap, markerInfo: MarkerInfo) {
        val currentList = markerInfo.fotos
        currentList?.add(photo)
    }

    fun removeMarker(markerInfo: MarkerInfo) {
        val currentList = _markers.value?.toMutableList()
        currentList?.remove(markerInfo)
        _markers.value = currentList
    }

}