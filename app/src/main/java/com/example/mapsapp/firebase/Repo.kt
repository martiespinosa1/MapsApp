package com.example.mapsapp.firebase

import android.util.Log
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
                    "latitude" to marker.latitude,
                    "longitude" to marker.longitude,
                    "type" to marker.type,
                    "photos" to marker.photos,
                    "userId" to marker.userId,
                    "markerId" to marker.markerId
                )
            )
    }

    fun editMarker(editedMarker: MarkerInfo) {

    }

    fun deleteMarker(markerId: String) {
        database.collection("markers").whereEqualTo("markerId", markerId).get()
            .addOnSuccessListener { markers ->
                for (marker in markers) {
                    database.collection("markers").document(marker.id).delete()
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Error", "Error getting documents: ", exception)
            }
    }

    fun getMarkers(): CollectionReference {
        return database.collection("markers")
    }

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