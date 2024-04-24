package com.example.mapsapp.view

import android.Manifest
import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mapsapp.model.MarkerInfo
import com.example.mapsapp.viewmodel.ViewModel
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mapsapp.navigation.Routes
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.mapsapp.R
import com.google.android.gms.maps.model.LatLng

@Composable
fun MarkerList(myViewModel: ViewModel, navController: NavController) {
    val markerState by myViewModel.markerList.observeAsState()
    if (markerState?.size == 0) myViewModel.lastKnownLocation = null
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
    if (showPermissionDenied) {
        PermisionDeclinedScreen()
    }



    val filteredMarkers by myViewModel.filteredMarkers.observeAsState()

    if (myViewModel.filterType.value == "" || myViewModel.filterType.value == "All") {
        myViewModel.filteredMarkers.value = myViewModel.markerList.value!!
    } else if (myViewModel.filterType.value == "Other") {
        myViewModel.getMarkersOfAType(myViewModel.userId.value ?: "", "Type") { filteredMarkers ->
            myViewModel.filteredMarkers.value = filteredMarkers.toMutableList()
        }
    } else {
        myViewModel.getMarkersOfAType(myViewModel.userId.value ?: "", myViewModel.filterType.value ?: "") { filteredMarkers ->
            myViewModel.filteredMarkers.value = filteredMarkers.toMutableList()
        }
    }



    if (markerState == null || (markerState?.size ?: 0) == 0) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(myViewModel.myColor2),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "No markers yet", color = Color.White)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(myViewModel.myColor2)
        ) {
            items(myViewModel.filteredMarkers.value!!.size) { index ->
                val currentMarker = myViewModel.filteredMarkers.value?.getOrNull(index)
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
    var editingMode by remember { mutableStateOf(false) }
    var editTextField by remember { mutableStateOf(marker.name) }

    Card(
        onClick = {
            navController.navigate(Routes.Map.route) {
                myViewModel.currentMarker = marker
                myViewModel.comingFromList.value = true
                launchSingleTop = true
                popUpTo(Routes.Map.route) {
                    saveState = true
                }
            }
        },
        colors = CardDefaults.cardColors(containerColor = myViewModel.myColor1),
        border = BorderStroke(3.dp, Color.White),
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
                    Image(painter = painterResource(id = R.drawable.marker_icon), contentDescription = "marker icon", modifier = Modifier
                        .size(100.dp)
                        .padding(start = 16.dp))
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.padding(16.dp)
                ) {
                    if (!editingMode) {
                        Text(
                            text = editTextField,
                            fontSize = 23.sp,
                            color = Color.White
                        )
                    } else {
                        Row {
                            TextField(
                                value = editTextField,
                                onValueChange = { editTextField = it },
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .clip(RoundedCornerShape(32.dp)),
                                colors = TextFieldDefaults.textFieldColors(
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                )
                            )
                            Spacer(modifier = Modifier.size(12.dp))
                            Button(
                                onClick = {
                                    editingMode = false
                                    if (editTextField != marker.name) {
                                        marker.name = editTextField
                                        myViewModel.editMarker(marker)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(myViewModel.myColor2, Color.White)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Check", tint = Color.White)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(4.dp))
                    if (marker.type != "Type") {
                        Text(
                            text = marker.type,
                            fontSize = 18.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.size(12.dp))
                    Row {
                        Button(
                            onClick = {
                                myViewModel.actualMarker.value = marker
                                navController.navigate(Routes.TakePhoto.route)
                            },
                            colors = ButtonDefaults.buttonColors(myViewModel.myColor2, Color.White)
                        ) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = "Camera", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = { editingMode = true },
                            colors = ButtonDefaults.buttonColors(myViewModel.myColor2, Color.White)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = {
                                myViewModel.removeMarker(marker)
                            },
                            colors = ButtonDefaults.buttonColors(myViewModel.myColor2, Color.White)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                        }
                    }
                }
            }
            if (!marker.photos.isNullOrEmpty()) {
                myViewModel.getMarker(marker.markerId)
                LazyRow {
                    marker.photos.let {
                        items(it.size) { index ->
                            GlideImage(
                                model = it[index],
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