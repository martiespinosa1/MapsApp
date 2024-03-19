package com.example.mapsapp.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.navigation.NavController
import com.example.mapsapp.navigation.Routes

import com.example.mapsapp.viewmodel.ViewModel

//@Composable
//fun Camera(myViewModel: ViewModel, navController: NavController) {
//    val context = LocalContext.current
//    val isCameraPermissionGranted by myViewModel.cameraPermissionGrented.observeAsState(false)
//    val shouldShowPermissionRationale by myViewModel.shouldShowPermissionRationale.observeAsState(false)
//    val showPermissionDenied by myViewModel.showPermissionDenied.observeAsState(false)
//
//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission(),
//        onResult = { isGranted ->
//            if (isGranted) {
//                myViewModel.setCameraPermissionGranted(true)
//            } else {
//                myViewModel.setShouldShowPermissionRationale(
//                    shouldShowRequestPermissionRationale(
//                        context as Activity,
//                        Manifest.permission.CAMERA
//                    )
//                )
//                if (!shouldShowPermissionRationale) {
//                    Log.i("Camera", "No podemos volver a pedir permisos")
//                    myViewModel.setShowPermissionDenied(true)
//                }
//            }
//        }
//    )
//
//    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()
//    ) {
//        Button(onClick = {
//            if (!isCameraPermissionGranted) {
//                launcher.launch(Manifest.permission.CAMERA)
//            } else {
//                navController.navigate(Routes.TakePhoto.route)
//            }
//        }) {
//            Text(text = "Take photo")
//        }
//    }
//    if (showPermissionDenied) {
//        PermisionDeclinedScreen()
//    }
//}
//
//@Composable
//fun PermisionDeclinedScreen() {
//    val context = LocalContext.current
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()
//    ) {
//        Text(text = "Permission requiered", fontWeight = FontWeight.Bold)
//        Text(text = "This app needs access to the camera to take photos")
//        Button(onClick = {
//            openAppSettings(context as Activity)
//        }) {
//            Text(text = "Accept")
//        }
//    }
//}
//
//fun openAppSettings(activity: Activity) {
//    val intent = Intent().apply {
//        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//        data = Uri.fromParts("package", activity.packageName, null)
//        flags = Intent.FLAG_ACTIVITY_NEW_TASK
//    }
//}