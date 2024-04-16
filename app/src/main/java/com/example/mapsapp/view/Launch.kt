package com.example.mapsapp.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mapsapp.R
import com.example.mapsapp.navigation.Routes
import kotlinx.coroutines.delay


@Composable
fun Launch(alphaAnim: Float) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.map_logo),
            contentDescription = "logo",
            alpha = alphaAnim,
            modifier = Modifier
                .requiredSize(250.dp)
                .clip(shape = RoundedCornerShape(25.dp))
                .align(Alignment.CenterHorizontally)
                .alpha(alphaAnim)
        )

        Text(
            text = "Maps App",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 30.dp).alpha(alphaAnim)
        )
    }
}


@Composable
fun LaunchAnimation(navController: NavController) {
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 3000), label = ""
    )
    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(2000)
        navController.popBackStack()
        navController.navigate(Routes.Map.route)
    }
    Launch(alphaAnim.value)
}