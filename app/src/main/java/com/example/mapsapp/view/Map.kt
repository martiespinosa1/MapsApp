package com.example.mapsapp.view

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mapsapp.MainActivity
import com.example.mapsapp.MyDrawer
import com.example.mapsapp.MyScaffold
import com.example.mapsapp.model.MarkerInfo
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.viewmodel.ViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Map(myViewModel: ViewModel, navController: NavController) {
    //val markers = remember { mutableStateListOf<MarkerInfo>() }
    val myMarkers: List<MarkerInfo> by myViewModel.markers.observeAsState(emptyList())
    val (showPopup, setShowPopup) = remember { mutableStateOf(false) }
    val (popupCoordinates, setPopupCoordinates) = remember { mutableStateOf(LatLng(0.0, 0.0)) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val itb = LatLng(41.4534265, 2.1837151)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(itb, 10f)
        }
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false ,
                myLocationButtonEnabled = true
            ),
            onMapLongClick = { coordinates ->
                setPopupCoordinates(coordinates)
                setShowPopup(true)
            }
        ) {
//            Marker(
//                state = MarkerState(position = itb),
//                title = "ITB",
//                snippet = "Fucking Burpees"
//            )

            val context = LocalContext.current
            val fusedLocationProviderClient = remember { LocationServices.getFusedLocationProviderClient(context) }
            var lastKnownLocation by remember { mutableStateOf<Location?>(null) }
            var deviceLatLng by remember { mutableStateOf(LatLng(0.0, 0.0)) }
            val cameraPositionState = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(deviceLatLng, 18f) }
            val locationResult = fusedLocationProviderClient.getCurrentLocation(100, null)
            locationResult.addOnCompleteListener(context as MainActivity) { task ->
                if (task.isSuccessful) {
                    lastKnownLocation = task.result
                    deviceLatLng = LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(deviceLatLng, 18f)
                } else {
                    Log.e("Error", "Exception: %s", task.exception)
                }
            }

            myMarkers.forEach { coordinates ->
                myMarkers.forEach { markerInfo ->
                    Marker(
                        state = MarkerState(position = markerInfo.coordinates),
                        title = markerInfo.name,
                        snippet = "Type: ${markerInfo.type}"
                    )
                }
            }
        }

//        Button(
//            onClick = { navController.navigate(Routes.AddMarker.route) },
//            modifier = Modifier
//                .align(Alignment.BottomEnd)
//                .padding(16.dp)
//        ) {
//            Icon(Icons.Filled.Add, contentDescription = "Add marker", tint = Color.White)
//        }

        if (showPopup) {
            PopupWithTextField(
                onDismiss = { setShowPopup(false) },
                onTextFieldSubmitted = { name, type ->
                    val currentMarkers = myViewModel.markers.value ?: mutableListOf()
                    currentMarkers.add(MarkerInfo(name = name, coordinates = popupCoordinates, type = type))
                    myViewModel.markers.value = currentMarkers
                    setShowPopup(false)
                }
            )
        }



    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PopupWithTextField(
    onDismiss: () -> Unit,
    onTextFieldSubmitted: (String, String) -> Unit
) {
    var textFieldValue by remember { mutableStateOf("") }
    val expanded = remember { mutableStateOf(false) }
    val type = remember { mutableStateOf("Otro") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier
                .width(300.dp)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                TextField(
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it },
                    label = { Text("Enter marker name") },
                    modifier = Modifier.fillMaxWidth(0.8f)
                )

                OutlinedButton(onClick = { expanded.value = true }) {
                    Text("Tipo: ${type.value}")
                }

                DropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Otro") },
                        onClick = {
                            type.value = "Otro"
                            expanded.value = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Montaña") },
                        onClick = {
                            type.value = "Montaña"
                            expanded.value = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Bar") },
                        onClick = {
                            type.value = "Bar"
                            expanded.value = false
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onTextFieldSubmitted(textFieldValue, type.value)
                        onDismiss()
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Submit")
                }
            }
        }
    }
}