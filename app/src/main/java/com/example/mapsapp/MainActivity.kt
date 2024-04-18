package com.example.mapsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.ui.theme.MapsAppTheme
import com.example.mapsapp.view.AddMarker
import com.example.mapsapp.view.LaunchAnimation
import com.example.mapsapp.view.LogIn
import com.example.mapsapp.view.Map
import com.example.mapsapp.view.MarkerList
import com.example.mapsapp.view.TakePhoto
import com.example.mapsapp.viewmodel.ViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MapsAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val myViewModel = ViewModel()
                    val navigationController = rememberNavController()

                    // location permission
                    val localizationPermissionState = rememberPermissionState(permission = android.Manifest.permission.ACCESS_FINE_LOCATION)
                    LaunchedEffect(Unit) {
                        localizationPermissionState.launchPermissionRequest()
                    }
                    if(localizationPermissionState.status.isGranted) {
                        MyDrawer(myViewModel, navigationController)
                    } else {
                        Text("Need permission")
                    }
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDrawer (myViewModel: ViewModel, navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val scope = rememberCoroutineScope()
    val state: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    ModalNavigationDrawer(modifier = Modifier.fillMaxWidth(), drawerState = state, gesturesEnabled = false, drawerContent = {
        ModalDrawerSheet {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(
                    onClick = {
                        scope.launch {
                            state.close()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close"
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PersonOutline,
                    contentDescription = "Icono de usuario",
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    "${myViewModel.loggedUser.value}",
                    modifier = Modifier.padding(start = 8.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }
            Divider()
            NavigationDrawerItem(
                label = { Text(text = "View marker list") },
                selected = false,
                onClick = {
                    scope.launch {
                        state.close()
                    }
                    if (currentRoute != Routes.MarkerList.route) {
                        navController.navigate(Routes.MarkerList.route)
                    }
                }
            )
            NavigationDrawerItem(
                label = { Text(text = "View map") },
                selected = false,
                onClick = {
                    scope.launch {
                        state.close()
                    }
                    if (currentRoute != Routes.Map.route) {
                        navController.navigate(Routes.Map.route)
                    }
                }
            )
            NavigationDrawerItem(
                label = {
                    Text(text = "Log out", fontWeight = FontWeight.Bold)
                },
                selected = false,
                onClick = {
                    myViewModel.logout()
                    scope.launch {
                        state.close()
                    }
                    navController.navigate(Routes.Login.route)
                }
            )
        }
    }) {
        MyScaffold (myViewModel, state, navController)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(myViewModel: ViewModel, state: DrawerState) {
    val scope = rememberCoroutineScope()

    TopAppBar(
        title = { Text(text = "Maps App") },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.DarkGray,
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
        navigationIcon = {
            IconButton(
                onClick = {
                    scope.launch {
                        state.open()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu"
                )
            }
        }
    )
}

@Composable
fun MyScaffold(myViewModel: ViewModel, state: DrawerState, navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = { MyTopAppBar(myViewModel, state) },
        bottomBar = { },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                NavHost(
                    navController = navController as NavHostController,
                    startDestination = Routes.Launch.route
                ) {
                    composable(Routes.Launch.route) { LaunchAnimation(myViewModel, navController) }
                    composable(Routes.Login.route) { LogIn(myViewModel, navController) }
                    composable(Routes.Map.route) { Map(myViewModel, navController) }
                    composable(Routes.MarkerList.route) { MarkerList(myViewModel, navController) }
                    composable(Routes.TakePhoto.route) { TakePhoto(myViewModel, navController) }
                }
            }
        }
    )
}
