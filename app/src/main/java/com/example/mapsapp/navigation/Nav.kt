package com.example.mapsapp.navigation

sealed class Routes(val route: String) {
    object Launch: Routes("Launch")
    object Login: Routes("login")
    object Map: Routes("Map")
    object AddMarker: Routes("AddMarker")
    object MarkerList: Routes("MarkerList")
    object TakePhoto: Routes("TakePhoto")
}