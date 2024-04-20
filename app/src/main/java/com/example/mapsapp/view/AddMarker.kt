package com.example.mapsapp.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.mapsapp.R
import com.example.mapsapp.firebase.Repo
import com.example.mapsapp.model.MarkerInfo
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.viewmodel.ViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun AddMarker(
    myViewModel: ViewModel,
    navController: NavController,
    onDismiss: () -> Unit,
    onTextFieldSubmitted: (String, String, MutableList<String>?) -> Unit
) {
    var textFieldValue by rememberSaveable { mutableStateOf("") }
    val expanded = rememberSaveable { mutableStateOf(false) }
    val typeOptions = listOf<String>("Shop", "Resuaturant", "Pub")
    val typeChoosed = rememberSaveable { mutableStateOf("Type") }

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

    val img: Bitmap? = ContextCompat.getDrawable(context, R.drawable.empty_image)?.toBitmap()
    var bitmap by remember { mutableStateOf(img) }
    val launchImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            if (Build.VERSION.SDK_INT < 28) {
                bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                if (it != null) {
                    myViewModel.uploadImage(it)
                }
            } else {
                val source = it?.let { itl ->
                    ImageDecoder.createSource(context.contentResolver, itl) }

                source?.let { itl ->  ImageDecoder.decodeBitmap(itl) }
                if (it != null) {
                    myViewModel.uploadImage(it)
                }
            }
        }
    )

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .padding(bottom = 32.dp)
        ) {
            Column {
                Text(
                    text = "Add Marker",
                    fontSize = 32.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                TextField(
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it },
                    label = { Text("Marker Name", fontFamily = myViewModel.myFontFamily) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(32.dp))
                )
                OutlinedButton(
                    onClick = { expanded.value = true },
                    modifier = Modifier.clip(shape = MaterialTheme.shapes.small),
                    colors = ButtonDefaults.buttonColors(Color.Transparent, myViewModel.myColor2)
                ) {
                    Text(typeChoosed.value, fontFamily = myViewModel.myFontFamily)
                }
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false }
                    ) {
                        for (type in typeOptions) {
                            DropdownMenuItem(
                                text = { Text(type, fontFamily = myViewModel.myFontFamily) },
                                onClick = {
                                    typeChoosed.value = type
                                    expanded.value = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = {
                        myViewModel.changeTakePhotoFromCreateMarker(true)
                        navController.navigate(Routes.TakePhoto.route)
                    },
                        colors = ButtonDefaults.buttonColors(myViewModel.myColor2, Color.White)
                    ) {
                        Text("Take photo", fontFamily = myViewModel.myFontFamily)
                    }
                    Button(onClick = {
                        launchImage.launch("image/*")
                    },
                        colors = ButtonDefaults.buttonColors(myViewModel.myColor2, Color.White)
                    ) {
                        Text("Gallery", fontFamily = myViewModel.myFontFamily)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyRow {
                    items(myViewModel.photosInTransit.size) { index ->
                        GlideImage(
                            model = myViewModel.photosInTransit[index],
                            contentDescription = "Marker's Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        // Sube todas las fotos en photosInTransit al Storage
                        myViewModel.photosInTransit.forEach { photoUriString ->
                            val photoUri = Uri.parse(photoUriString)
                            myViewModel.uploadImage(photoUri)
                        }
                        // Limpia la lista de fotos en tránsito después de subirlas
                        myViewModel.photosInTransit = mutableListOf()
                        // Continúa con la lógica de guardar el marcador
                        onTextFieldSubmitted(textFieldValue, typeChoosed.value, myViewModel.photosInTransit)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(myViewModel.myColor2, Color.White)
                ) {
                    Text("Save", fontFamily = myViewModel.myFontFamily)
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

fun saveMarkerToFirebase(marker: MarkerInfo) {
    val repo = Repo()
    repo.addMarker(marker)
}
