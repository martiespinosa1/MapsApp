package com.example.mapsapp.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.example.mapsapp.MainActivity
import com.example.mapsapp.model.MarkerInfo
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.viewmodel.ViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
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
//        val itb = LatLng(41.4534265, 2.1837151)
//        val cameraPositionState = rememberCameraPositionState {
//            position = CameraPosition.fromLatLngZoom(itb, 10f)
//        }

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

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false ,
                myLocationButtonEnabled = true
            ),
            properties = MapProperties(isMyLocationEnabled = true, isBuildingEnabled = true),

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
            PopupWithTextField(myViewModel, navController,
                onDismiss = { setShowPopup(false) },
                onTextFieldSubmitted = { name, type, foto ->
                    val currentMarkers = myViewModel.markers.value ?: mutableListOf()
                    mutableListOf(foto)?.let { MarkerInfo(name = name, coordinates = popupCoordinates, type = type, fotos = it) }
                        ?.let { currentMarkers.add(it) }
                    myViewModel.markers.value = currentMarkers
                    setShowPopup(false)
                }
            )
        }



    }
}



@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun PopupWithTextField(
    myViewModel: ViewModel,
    navController: NavController,
    onDismiss: () -> Unit,
    onTextFieldSubmitted: (String, String, Bitmap?) -> Unit
) {
    var textFieldValue by remember { mutableStateOf("") }
    val expanded = remember { mutableStateOf(false) }
    val type = remember { mutableStateOf("Otro") }
    val fotos = remember { mutableStateOf(mutableListOf<Bitmap>()) }

    val context = LocalContext.current
    val isCameraPermissionGranted by myViewModel.cameraPermissionGrented.observeAsState(false)
    val shouldShowPermissionRationale by myViewModel.shouldShowPermissionRationale.observeAsState(false)
    val showPermissionDenied by myViewModel.showPermissionDenied.observeAsState(false)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            myViewModel.setCameraPermissionGranted(true)
            navController.navigate(Routes.TakePhoto.route)
        } else {
            myViewModel.setShouldShowPermissionRationale(
                ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity,
                    Manifest.permission.CAMERA
                )
            )
            if (!shouldShowPermissionRationale) {
                Log.i("Camera", "No podemos volver a pedir permisos")
                myViewModel.setShowPermissionDenied(true)
            }
        }
    }

    // camera permissions
//    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
//    LaunchedEffect(Unit) {
//        cameraPermissionState.launchPermissionRequest()
//    }
//    if(cameraPermissionState.status.isGranted) {
//        TakePhoto(myViewModel, navController)
//    } else {
//        Text("Need permission")
//    }

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
                Button(onClick = {
                    if (!isCameraPermissionGranted) {
                        launcher.launch(Manifest.permission.CAMERA)
                    } else {
                        navController.navigate(Routes.TakePhoto.route)
                    }
                }
                ) {
                    Text(text = "Go to Take photo")
                }

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
                        var foto: Bitmap? = null
                        if (fotos.value.size > 0) {
                            foto = fotos.value[0]
                        }
                        onTextFieldSubmitted(textFieldValue, type.value, foto)
                        onDismiss()
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Submit")
                }
            }
            if (showPermissionDenied) {
                PermisionDeclinedScreen()
            }
        }
    }
}




@Composable
fun PermisionDeclinedScreen() {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Permission requiered", fontWeight = FontWeight.Bold)
        Text(text = "This app needs access to the camera to take photos")
        Button(onClick = {
            openAppSettings(context as Activity)
        }) {
            Text(text = "Accept")
        }
    }
}

fun openAppSettings(activity: Activity) {
    val intent = Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", activity.packageName, null)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
}