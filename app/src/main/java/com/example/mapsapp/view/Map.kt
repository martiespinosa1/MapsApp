package com.example.mapsapp.view

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.mapsapp.navigation.Routes
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@Composable
fun Map(navController: NavController) {
    val markers = remember { mutableStateListOf<MarkerInfo>() }
    val (showPopup, setShowPopup) = remember { mutableStateOf(false) }
    val (popupCoordinates, setPopupCoordinates) = remember { mutableStateOf(LatLng(0.0, 0.0)) }

    Box(
        modifier = Modifier.fillMaxSize()
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
            Marker(
                state = MarkerState(position = itb),
                title = "ITB",
                snippet = "Marker at ITB"
            )

            markers.forEach { coordinates ->
                markers.forEach { markerInfo ->
                    Marker(
                        state = MarkerState(position = markerInfo.coordinates),
                        title = markerInfo.name,
                        snippet = "Marker at ${markerInfo.name}"
                    )
                }
            }
        }

        Button(
            onClick = { navController.navigate(Routes.AddMarker.route) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add marker", tint = Color.White)
        }

        if (showPopup) {
            PopupWithTextField(
                onDismiss = { setShowPopup(false) },
                onTextFieldSubmitted = { name ->
                    markers.add(MarkerInfo(name = name, coordinates = popupCoordinates))
                    setShowPopup(false)
                }
            )
        }


    }
}

data class MarkerInfo(val name: String, val coordinates: LatLng)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PopupWithTextField(
    onDismiss: () -> Unit,
    onTextFieldSubmitted: (String) -> Unit
) {
    var textFieldValue by remember { mutableStateOf("") }

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
                    label = { Text("Enter Text") },
                    modifier = Modifier.fillMaxWidth(0.8f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onTextFieldSubmitted(textFieldValue)
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