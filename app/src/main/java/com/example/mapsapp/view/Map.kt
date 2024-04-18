package com.example.mapsapp.view

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.mapsapp.MainActivity
import com.example.mapsapp.model.MarkerInfo
import com.example.mapsapp.viewmodel.ViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@SuppressLint("MissingPermission")
@Composable
fun Map(myViewModel: ViewModel, navController: NavController) {
    val myMarkers: List<MarkerInfo> by myViewModel.markers.observeAsState(emptyList())
    val isPopupVisible by myViewModel.isPopupVisible.observeAsState()
    val (popupCoordinates, setPopupCoordinates) = rememberSaveable { mutableStateOf(LatLng(0.0, 0.0)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val context = LocalContext.current
        val fusedLocationProviderClient = remember { LocationServices.getFusedLocationProviderClient(context) }
        var lastKnownLocation by remember { mutableStateOf<Location?>(null) }
        //var deviceLatLng by remember { mutableStateOf(LatLng(0.0, 0.0)) }
        val cameraPositionState = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(myViewModel.deviceLatLng.value!!, 18f) }
        val locationResult = fusedLocationProviderClient.getCurrentLocation(100, null)
        // Obtener la ubicación actual solo si lastKnownLocation es null
        if (lastKnownLocation == null) {
            val locationResult = fusedLocationProviderClient.getCurrentLocation(100, null)
            locationResult.addOnCompleteListener(context as MainActivity) { task ->
                if (task.isSuccessful) {
                    lastKnownLocation = task.result
                    myViewModel.deviceLatLng.value = LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(myViewModel.deviceLatLng.value!!, 18f)
                } else {
                    Log.e("Error", "Exception: %s", task.exception)
                }
            }
        }



        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = true
            ),
            properties = MapProperties(
                isMyLocationEnabled = true,
                isBuildingEnabled = true
            ),
            onMapLongClick = { coordinates ->
                setPopupCoordinates(coordinates)
                myViewModel.deviceLatLng.value = popupCoordinates
                myViewModel.changePopUpVisibility(true)
            }
        ) {
            myMarkers.forEach { marker ->
                Marker(
                    state = MarkerState(position = marker.coordinates),
                    title = marker.name,
                    snippet = "Type: ${marker.type}"
                )
            }
        }

        if (isPopupVisible == true) {
            AddMarker(myViewModel, navController,
                onDismiss = { myViewModel.changePopUpVisibility(false) },
                onTextFieldSubmitted = { name, type, photos ->
                    val currentMarkers = myViewModel.markers.value ?: mutableListOf()
                    val newMarker = MarkerInfo(name = name, coordinates = popupCoordinates, type = type, photos = photos, userId = null)
                    currentMarkers.add(newMarker)
                    myViewModel.markers.value = currentMarkers
                    myViewModel.changePopUpVisibility(false)
                    saveMarkerToFirebase(newMarker)
                }
            )
        }
    }
}
