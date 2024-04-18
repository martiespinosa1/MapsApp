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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.viewmodel.ViewModel

@Composable
fun LogIn(myViewModel: ViewModel, navController: NavController) {
    var textUserEmail = remember { mutableStateOf(TextFieldValue("")) }
    var textUserPassword = remember { mutableStateOf(TextFieldValue("")) }

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
            Column {
                Text(
                    text = "Login",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                TextField(
                    value = textUserEmail.value,
                    onValueChange = { textUserEmail.value = it },
                    label = { Text("Email") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Icono de correo electrónico") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                    modifier = Modifier.padding(vertical = 10.dp)
                )
                TextField(
                    value = textUserPassword.value,
                    onValueChange = { textUserPassword.value = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Icono de contraseña") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.padding(vertical = 10.dp)
                )

                Button(onClick = {
                    myViewModel.login(textUserEmail.value.text, textUserPassword.value.text)
                    if (myViewModel.goToNext.value == true) { navController.navigate(Routes.Map.route) }
                },
                    Modifier.width(300.dp)) {
                    Text(
                        text = "Log In",
                        fontSize = 18.sp
                    )
                }
            }
        }

        Button(onClick = {
            myViewModel.register(textUserEmail.value.text, textUserPassword.value.text)
            if (myViewModel.goToNext.value == true) { navController.navigate(Routes.Map.route) }
        },
            Modifier.width(300.dp)) {
            Text(
                text = "Register",
                fontSize = 18.sp
            )
        }
    }
}


//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun LogInPreview() {
//    LogIn()
//}
