package com.example.mapsapp.view

import android.Manifest
import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mapsapp.model.MarkerInfo
import com.example.mapsapp.viewmodel.ViewModel
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mapsapp.navigation.Routes
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MarkerList(myViewModel: ViewModel, navController: NavController) {
    val markerState by myViewModel.markers.observeAsState()

    val context = LocalContext.current
    val isCameraPermissionGranted by myViewModel.cameraPermissionGrented.observeAsState(false)
    val shouldShowPermissionRationale by myViewModel.shouldShowPermissionRationale.observeAsState(false)
    val showPermissionDenied by myViewModel.showPermissionDenied.observeAsState(false)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                myViewModel.setCameraPermissionGranted(true)
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
    )
    // camera permission
//    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
//    LaunchedEffect(Unit) {
//        cameraPermissionState.launchPermissionRequest()
//    }
//    if(cameraPermissionState.status.isGranted) {
//        TakePhoto(myViewModel, navController)
//    } else {
//        Text("Need permission")
//    }

//    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()
//    ) {
//        Button(onClick = {
//            if (!isCameraPermissionGranted) {
//                Log.i("Permission", "NOT GRANTED")
//                launcher.launch(Manifest.permission.CAMERA)
//            } else {
//                Log.i("Permission", "GRANTED")
//                navController.navigate(Routes.TakePhoto.route)
//            }
//        }) {
//            Text(text = "Take photo")
//        }
//    }
    if (showPermissionDenied) {
        PermisionDeclinedScreen()
    }



    if (markerState == null || (markerState?.size ?: 0) == 0) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "No markers yet")
        }
    } else {
        LazyColumn() {
            items(myViewModel.markers.value?.size ?: 0) { index ->
                val currentMarker = markerState?.getOrNull(index)
                if (currentMarker != null) {
                    MarkerItem(
                        marker = currentMarker,
                        navController = navController,
                        myViewModel = myViewModel
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun MarkerItem(marker: MarkerInfo, navController: NavController, myViewModel: ViewModel) {
    Card(
        onClick = {
            navController.navigate(Routes.Map.route) {
                launchSingleTop = true
                popUpTo(Routes.Map.route) {
                    saveState = true
                }
//                navController.graph.startDestinationRoute?.let { route ->
//                    navigate(route) {
//                        putParcelable("marker", marker)
//                    }
//                }
            }
        },
        border = BorderStroke(3.dp, Color.LightGray),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Location",
                        tint = Color.Red,
                        modifier = Modifier.size(100.dp)
                    )
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.padding(16.dp)
                ) {
                    if (marker.name != "") {
                        Text(
                            text = marker.name,
                            //color = Color.LightGray,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.size(4.dp))
                    if (marker.type != "Type") {
                        Text(
                            text = marker.type,
                            //color = Color.LightGray,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.size(12.dp))
                    Button(onClick = {
                        myViewModel.currentMarker = marker
                        navController.navigate(Routes.TakePhoto.route)
                    }) {
                        Text("Take photo")
                    }

                    Spacer(modifier = Modifier.size(12.dp))
                    Button(onClick = {
                        // Eliminar el marcador
                        myViewModel.removeMarker(marker)
                    }) {
                        Text("Delete Marker")
                    }
                }
            }
            if (!marker.photos.isNullOrEmpty()) {
                LazyRow {
                    marker.photos?.let {
                        items(it.size) { index ->
                            GlideImage(
                                model = marker.photos[index],
                                contentDescription = "Marker's Photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(100.dp)
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        }
                    }
                }
            }
        }
    }
}





