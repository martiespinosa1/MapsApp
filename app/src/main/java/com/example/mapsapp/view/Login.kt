package com.example.mapsapp.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mapsapp.firebase.UserPrefs
import com.example.mapsapp.navigation.Routes
import com.example.mapsapp.viewmodel.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogIn(myViewModel: ViewModel, navController: NavController) {
    var textUserName = remember { mutableStateOf(TextFieldValue("")) }
    var textUserEmail = remember { mutableStateOf(TextFieldValue("")) }
    var textUserPassword = remember { mutableStateOf(TextFieldValue("")) }
    var textUserPasswordRepeat = remember { mutableStateOf(TextFieldValue("")) }
    var passwordVisible = remember { mutableStateOf(false) }
    var passwordVisibleRepeat = remember { mutableStateOf(false) }

    val registering by myViewModel.registering.observeAsState()

    var rememberUser by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val userPrefs = UserPrefs(context)
    val storeUserData = userPrefs.getUserData.collectAsState(initial = emptyList())

    if (storeUserData.value.isNotEmpty() && storeUserData.value[0] != "" && storeUserData.value[1] != "") {
        myViewModel.modifyProcessing()
        myViewModel.login(storeUserData.value[0], storeUserData.value[1])
        navController.navigate(Routes.Map.route)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(myViewModel.myColor2),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(300.dp)
                .height(if (registering == true) 500.dp else 350.dp)
                .background(myViewModel.myColor1, shape = RoundedCornerShape(15.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = if (registering == true) "Sign up" else "Log in",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                if (registering == true) {
                    TextField(
                        value = textUserName.value,
                        onValueChange = { textUserName.value = it },
                        label = { Text("Name", fontFamily = myViewModel.myFontFamily, style = TextStyle(fontSize = 12.sp)) },
                        leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = "user icon") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                        textStyle = TextStyle(fontFamily = myViewModel.myFontFamily, fontSize = 12.sp),
                        modifier = Modifier.padding(vertical = 10.dp),
                    )
                }
                TextField(
                    value = textUserEmail.value,
                    onValueChange = { textUserEmail.value = it },
                    label = { Text("Email", fontFamily = myViewModel.myFontFamily, style = TextStyle(fontSize = 12.sp)) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "email icon") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                    textStyle = TextStyle(fontFamily = myViewModel.myFontFamily, fontSize = 12.sp),
                    modifier = Modifier.padding(vertical = 10.dp)
                )
                TextField(
                    value = textUserPassword.value,
                    onValueChange = { textUserPassword.value = it },
                    label = { Text("Password", fontFamily = myViewModel.myFontFamily, style = TextStyle(fontSize = 12.sp)) },
                    leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "password icon") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                    visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                            Icon(imageVector = if (passwordVisible.value) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, contentDescription = if (passwordVisible.value) "Hide password" else "Show password")
                        }
                    },
                    textStyle = TextStyle(fontFamily = myViewModel.myFontFamily, fontSize = 12.sp),
                    modifier = Modifier.padding(vertical = 10.dp)
                )
                if (registering == true) {
                    TextField(
                        value = textUserPasswordRepeat.value,
                        onValueChange = { textUserPasswordRepeat.value = it },
                        label = { Text("Repeat password", fontFamily = myViewModel.myFontFamily, style = TextStyle(fontSize = 12.sp)) },
                        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "password icon") },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                        visualTransformation = if (passwordVisibleRepeat.value) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisibleRepeat.value = !passwordVisibleRepeat.value }) {
                                Icon(imageVector = if (passwordVisibleRepeat.value) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, contentDescription = if (passwordVisibleRepeat.value) "Hide password" else "Show password")
                            }
                        },
                        textStyle = TextStyle(fontFamily = myViewModel.myFontFamily, fontSize = 12.sp),
                        modifier = Modifier.padding(vertical = 10.dp),
//                        colors = TextFieldDefaults.textFieldColors(
//                            focusedIndicatorColor = myViewModel.myColor2,
//                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Remember me", fontFamily = myViewModel.myFontFamily, color = Color.White, style = TextStyle(fontSize = 12.sp))
                    Checkbox(
                        checked = rememberUser,
                        onCheckedChange = { rememberUser = it },
                        modifier = Modifier.padding(end = 8.dp),
                        colors = CheckboxDefaults.colors(checkedColor = myViewModel.myColor2, checkmarkColor = myViewModel.myColor1)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(onClick = {
                    if (registering == true) {
                        if (textUserPassword.value.text == textUserPasswordRepeat.value.text) {
                            myViewModel.register(
                                textUserEmail.value.text,
                                textUserPassword.value.text
                            )
                        }
                    } else {
                        myViewModel.login(textUserEmail.value.text, textUserPassword.value.text)
                    }
                    if (rememberUser) { CoroutineScope(Dispatchers.IO).launch { userPrefs.saveUserData(textUserName.value.text, textUserPassword.value.text) } }
                    if (myViewModel.goToNext.value == true) { navController.navigate(Routes.Map.route) }
                },
                    modifier = Modifier.width(300.dp),
                    colors = ButtonDefaults.buttonColors(myViewModel.myColor2, Color.White)
                ) {
                    if (myViewModel.showCircularProgressBar.value == true) {
                        CircularProgressIndicator()
                    } else {
                        Text(
                            text = if (registering == true) "Register" else "Log In",
                            fontFamily = myViewModel.myFontFamily,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = if (registering == true) "Already have an account? " else "Don't have an account? ", color = Color.White, fontSize = 12.sp)
            Text(
                text = if (registering == true) "Log in" else "Sign up",
                textDecoration = TextDecoration.Underline,
                fontSize = 12.sp,
                color = Color.White,
                modifier = Modifier
                    .clickable {
                        myViewModel.registering.value = !myViewModel.registering.value!!
                    }
            )
        }

        if (myViewModel.goToNext.value == true) {
            myViewModel.getUser(myViewModel.userId.value!!)
        }

        if (myViewModel.registerFail.value == true) {
            Toast.makeText(LocalContext.current, "Sign up failed", Toast.LENGTH_SHORT).show()
        }
        if (myViewModel.loginFail.value == true) {
            Toast.makeText(LocalContext.current, "Log in failed", Toast.LENGTH_SHORT).show()
        }

    }
}

