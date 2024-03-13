package com.example.mapsapp.viewmodel

import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapsapp.view.MarkerInfo
import com.google.accompanist.permissions.rememberPermissionState

class ViewModel: ViewModel() {

    // AQUI SE TIENEN QUE PONER COSAS QUE ESTAN EN OTRAS SCREENS

    val markers = { mutableStateListOf<MarkerInfo>() }



}