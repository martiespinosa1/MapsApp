package com.example.mapsapp.view

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.KeyboardType.Companion.Uri
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import com.example.mapsapp.R
import com.example.mapsapp.model.MarkerInfo
import com.example.mapsapp.viewmodel.ViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TakePhoto(myViewModel: ViewModel, navController: NavController) {
    val myMarkers: List<MarkerInfo> by myViewModel.markers.observeAsState(emptyList())

    val context = LocalContext.current
    val controller = remember {
        LifecycleCameraController(context).apply {
            CameraController.IMAGE_CAPTURE
        }
    }

    val img: Bitmap? = ContextCompat.getDrawable(context, R.drawable.empty_image)?.toBitmap()
    var bitmap by remember { mutableStateOf(img) }
    var uri by remember { mutableStateOf<Uri?>(null) }
    val launchImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                val source = it?.let { itl ->
                    ImageDecoder.createSource(context.contentResolver, itl)
                }
                source?.let { itl ->
                    ImageDecoder.decodeBitmap(itl)
                }
            }
            uri = it
        }
    )

    // camera permissions
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    LaunchedEffect(Unit) {
        cameraPermissionState.launchPermissionRequest()
    }
    if(cameraPermissionState.status.isGranted) {
        //TakePhoto(myViewModel, navController)

        Box(modifier = Modifier.fillMaxSize()) {
            CameraPreview(controller = controller, modifier = Modifier.fillMaxSize())
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .align(
                        Alignment.BottomCenter
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(
                        onClick = {
                            myViewModel.changeTakePhotoFromCreateMarker(false)
                            navController.popBackStack()
                        }
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = "Go back")
                    }
                    IconButton(
                        onClick = {
                            controller.cameraSelector =
                                if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                                    CameraSelector.DEFAULT_FRONT_CAMERA
                                } else {
                                    CameraSelector.DEFAULT_BACK_CAMERA
                                }
                        },
                    ) {
                        Icon(imageVector = Icons.Default.Cameraswitch, contentDescription = "Switch camera")
                    }
                    IconButton(onClick = {
                        launchImage.launch("*image/*")
                    }) {
                        Icon(imageVector = Icons.Default.Photo, contentDescription = "Open gallery")
                    }
                    IconButton(onClick = {
                        if (myViewModel.takePhotoFromCreateMarker.value == true) {
                            takePhoto(context, controller) { photo ->
                                myViewModel.photosInTransit.add(photo)
                            }
                        } else {
                            takePhoto(context, controller) { photo ->
                                addPotoToMarker(myViewModel.currentMarker, photo, myViewModel)
                            }
                        }
                    }) {
                        Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = "Take photo")
                    }
                }
            }
        }
    } else {
        Text("Need permission")
    }
}




private fun addPotoToMarker(marker: MarkerInfo, photo: Bitmap, myViewModel: ViewModel) {
    myViewModel.addPhoto(photo, marker)
}

private fun takePhoto(context: Context,
    controller: LifecycleCameraController, onPhotoTaken: (Bitmap) -> Unit) {
    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)
                val bitmap = rotateImageIfNeeded(image.toBitmap(), image.imageInfo.rotationDegrees)
                onPhotoTaken(bitmap)
                image.close()
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("Camera", "Error taken photo", exception)
            }
        }
    )
}



private fun rotateImageIfNeeded(bitmap: Bitmap, rotationDegrees: Int): Bitmap {
    return if (rotationDegrees != 0) {
        val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    } else {
        bitmap
    }
}


@Composable
fun CameraPreview(
    controller: LifecycleCameraController, modifier: Modifier = Modifier) {
    val lifeCycleOwner = LocalLifecycleOwner.current
    AndroidView(
        factory = {
            PreviewView(it).apply {
                this.controller = controller
                controller.bindToLifecycle(lifeCycleOwner)
            }
        }, modifier = modifier
    )
}



