package com.example.mapsapp.viewmodel

import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapsapp.model.MarkerInfo
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.LatLng

class ViewModel: ViewModel() {

    // AQUI SE TIENEN QUE PONER COSAS QUE ESTAN EN OTRAS SCREENS

    private var _marker = MutableLiveData(MarkerInfo("ITB", LatLng(41.4534265, 2.1837151), "itb"))
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