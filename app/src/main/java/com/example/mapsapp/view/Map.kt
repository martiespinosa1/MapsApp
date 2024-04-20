package com.example.mapsapp.view

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.example.mapsapp.MainActivity
import com.example.mapsapp.R
import com.example.mapsapp.model.MarkerInfo
import com.example.mapsapp.viewmodel.ViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@SuppressLint("MissingPermission")
@Composable
fun Map(myViewModel: ViewModel, navController: NavController) {
    val myMarkers: List<MarkerInfo> by myViewModel.markerList.observeAsState(emptyList())
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
        var cameraPositionState = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(myViewModel.deviceLatLng.value!!, 18f) }
        //val locationResult = fusedLocationProviderClient.getCurrentLocation(100, null)
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
        if (myViewModel.comingFromList.value == true) {
            cameraPositionState.position = CameraPosition.Builder().target(LatLng(myViewModel.currentMarker.latitude, myViewModel.currentMarker.longitude)).zoom(18f).build()
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
                //myViewModel.deviceLatLng.value = coordinates
                myViewModel.changePopUpVisibility(true)
            }
        ) {
            val customMarkerIcon = BitmapDescriptorFactory.fromResource(R.drawable.marker)
            myMarkers.forEach { marker ->
                Marker(
                    state = MarkerState(position = LatLng(marker.latitude, marker.longitude)),
                    title = marker.name,
                    snippet = "Type: ${marker.type}",
                    icon = customMarkerIcon
                )
            }
        }

        if (isPopupVisible == true) {
            AddMarker(myViewModel, navController,
                onDismiss = { myViewModel.changePopUpVisibility(false) },
                onTextFieldSubmitted = { name, type, photos ->
                    val currentMarkers = myViewModel.markerList.value ?: mutableListOf()
                    val newMarker = MarkerInfo(name = name, latitude = popupCoordinates.latitude, longitude = popupCoordinates.longitude, type = type, photos = photos, userId = null)
                    currentMarkers.add(newMarker)
                    myViewModel.markerList.value = currentMarkers
                    myViewModel.changePopUpVisibility(false)
                    saveMarkerToFirebase(newMarker)
                }
            )
        }

        Box(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            Image(painter = painterResource(id = R.drawable.plus_icon), contentDescription = "plus icon",
                Modifier
                    .size(60.dp)
                    .clickable {
                        myViewModel.changePopUpVisibility(true)
                    }
            )
        }

    }
}
