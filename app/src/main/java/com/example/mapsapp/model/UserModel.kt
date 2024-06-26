package com.example.mapsapp.model

data class UserModel(
    var userId: String? = null,
    var email: String,
    var password: String,
    var userName: String? = email.split("@")[0]
)