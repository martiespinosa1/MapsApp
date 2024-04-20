package com.example.mapsapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.viewmodel.ViewModel

@Composable
fun LogIn(myViewModel: ViewModel, navController: NavController) {
    var textUserName = remember { mutableStateOf(TextFieldValue("")) }
    var textUserEmail = remember { mutableStateOf(TextFieldValue("")) }
    var textUserPassword = remember { mutableStateOf(TextFieldValue("")) }
    var textUserPasswordRepeat = remember { mutableStateOf(TextFieldValue("")) }
    var passwordVisible = remember { mutableStateOf(false) }
    var passwordVisibleRepeat = remember { mutableStateOf(false) }

    val registering by myViewModel.registering.observeAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(300.dp)
                .height(if (registering == true) 500.dp else 350.dp)
                .background(Color.Blue.copy(alpha = 0.6f), shape = RoundedCornerShape(15.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = if (registering == true) "Register" else "Login",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                if (registering == true) {
                    TextField(
                        value = textUserName.value,
                        onValueChange = { textUserName.value = it },
                        label = { Text("Name") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = "user icon") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }
                TextField(
                    value = textUserEmail.value,
                    onValueChange = { textUserEmail.value = it },
                    label = { Text("Email") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "email icon") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                    modifier = Modifier.padding(vertical = 10.dp)
                )
                TextField(
                    value = textUserPassword.value,
                    onValueChange = { textUserPassword.value = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "password icon") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                    visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                            Icon(imageVector = if (passwordVisible.value) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, contentDescription = if (passwordVisible.value) "Hide password" else "Show password")
                        }
                    },
                    modifier = Modifier.padding(vertical = 10.dp)
                )
                if (registering == true) {
                    TextField(
                        value = textUserPasswordRepeat.value,
                        onValueChange = { textUserPasswordRepeat.value = it },
                        label = { Text("Repeat password") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "password icon") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                        visualTransformation = if (passwordVisibleRepeat.value) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisibleRepeat.value = !passwordVisibleRepeat.value }) {
                                Icon(imageVector = if (passwordVisibleRepeat.value) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, contentDescription = if (passwordVisibleRepeat.value) "Hide password" else "Show password")
                            }
                        },
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }

                Button(onClick = {
                    if (registering == true) {
                        myViewModel.register(textUserEmail.value.text, textUserPassword.value.text)
                    } else {
                        myViewModel.login(textUserEmail.value.text, textUserPassword.value.text)
                    }
                    if (myViewModel.goToNext.value == true) { navController.navigate(Routes.Map.route) }
                },
                    Modifier.width(300.dp)) {
                    Text(
                        text = if (registering == true) "Register" else "Log In",
                        fontSize = 18.sp
                    )
                }

                if (myViewModel.showCircularProgressBar.value == true) {
                    CircularProgressIndicator()
                }

                if (myViewModel.registerFail.value == true) {
                    Text(text = "Fallo de registro")
                }
                if (myViewModel.loginFail.value == true) {
                    Text(text = "Fallo de login")
                }
            }
        }

        Row {
            Text(text = if (registering == true) "Already have an account? " else "No account yet? ", fontSize = 18.sp)
            Text(
                text = if (registering == true) "Log in" else "Register",
                fontSize = 18.sp,
                color = Color.Blue,
                modifier = Modifier
                    .clickable {
                        myViewModel.registering.value = !myViewModel.registering.value!!
                    }
            )
        }

    }
}


//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun LogInPreview() {
//    LogIn()
//}
