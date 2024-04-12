package com.example.mapsapp.firebase

import com.example.mapsapp.model.MarkerInfo
import com.example.mapsapp.model.UserModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class Repo {
    private val database = FirebaseFirestore.getInstance()


    // --------------------- MARKERS ----------------------------
    fun addMarker(marker: MarkerInfo) {
        database.collection("markers")
            .add(
                hashMapOf(
                    "name" to marker.name,
                    "coordinates" to marker.coordinates,
                    "type" to marker.type,
                    "photos" to marker.photos,
                    "userId" to marker.userId,
                    "markerId" to marker.markerId
                )
            )
    }

    fun editMarker(editedMarker: MarkerInfo) {

    }

    fun deleteMarker(userId: String) {
    }

//    fun getMarkers(): CollectionReference {
//    }

    fun getMarker(markerId: String): DocumentReference {
        return database.collection("markers").document(markerId)
    }


    // ----------------------- USERS --------------------------
    fun addUser(user: UserModel) {
        database.collection("users")
            .add(
                hashMapOf(
                    "username" to user.email,
                    "password" to user.password
                )
            )
    }

    fun editUser(editedUser: UserModel) {
        database.collection("users").document(editedUser.userId!!).set(
            hashMapOf(
                "username" to editedUser.email,
                "password" to editedUser.password
            )
        )
    }

    fun deleteUser(userId: String) {
        database.collection("users").document(userId).delete()
    }

    fun getUsers(): CollectionReference {
        return database.collection("users")
    }

    fun getUser(userId: String): DocumentReference {
        return database.collection("users").document(userId)
    }

}