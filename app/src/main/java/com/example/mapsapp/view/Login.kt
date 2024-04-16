package com.example.mapsapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LogIn() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(300.dp)
                .height(350.dp)
                .background(Color.Blue.copy(alpha = 0.6f), shape = RoundedCornerShape(15.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Login",
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Button(onClick = { },
            Modifier.width(300.dp)) {
            Text(
                text = "Register",
                fontSize = 18.sp
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LogInPreview() {
    LogIn()
}
