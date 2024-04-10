package com.example.mapsapp.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mapsapp.firebase.Repo
import com.example.mapsapp.model.MarkerInfo
import com.example.mapsapp.model.UserModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange

class ViewModel: ViewModel() {

    // AQUI SE TIENEN QUE PONER COSAS QUE ESTAN EN OTRAS SCREENS

    private var _marker = MutableLiveData(MarkerInfo("ITB", LatLng(41.4534265, 2.1837151), "itb", null, ""))
    var marker = _marker

    private var _markers = MutableLiveData<MutableList<MarkerInfo>>()
    val markers = _markers

    private var _currentMarker: MarkerInfo = MarkerInfo("ITB", LatLng(41.4534265, 2.1837151), "itb", null, "")
    var currentMarker = _currentMarker

    private val _fotos = MutableLiveData<MutableList<Bitmap>>(mutableListOf())
    val fotos = _fotos

    private val _photosInTransit = mutableListOf<Bitmap>()
    var photosInTransit = _photosInTransit

    private var _takePhotoFromCreateMarker = MutableLiveData(false)
    var takePhotoFromCreateMarker = _takePhotoFromCreateMarker

    private var _isPopupVisible = MutableLiveData(false)
    var isPopupVisible = _isPopupVisible



    private val _cameraPermissionGrented = MutableLiveData(false)
    val cameraPermissionGrented = _cameraPermissionGrented

    private val _shouldShowPermissionRationale = MutableLiveData(false)
    val shouldShowPermissionRationale = _shouldShowPermissionRationale

    private val _showPermissionDenied = MutableLiveData(false)
    val showPermissionDenied = _showPermissionDenied

    fun setCameraPermissionGranted(granted: Boolean) {
        _cameraPermissionGrented.value = granted
    }

    fun setShouldShowPermissionRationale(should: Boolean) {
        _shouldShowPermissionRationale.value = should
    }

    fun setShowPermissionDenied(denied: Boolean) {
        _showPermissionDenied.value = denied
    }

    fun addPhoto(photo: Bitmap, markerInfo: MarkerInfo) {
        val currentList = markerInfo.photos
        currentList?.add(photo)
    }

    fun removeMarker(markerInfo: MarkerInfo) {
        val currentList = _markers.value?.toMutableList()
        currentList?.remove(markerInfo)
        _markers.value = currentList
    }

    fun changePopUpVisibility(value: Boolean) {
        _isPopupVisible.value = value
    }

    fun changeTakePhotoFromCreateMarker(value: Boolean) {
        _takePhotoFromCreateMarker.value = value
    }

    fun addImageToTransit(image: Bitmap) {
        _photosInTransit.add(image)
    }



    // FIREBASE
    // USERS
    private val repository = Repo()

    private var _userList: MutableLiveData<MutableList<UserModel>> = MutableLiveData(mutableListOf())
    var userList = _userList

    private var _actualUser: MutableLiveData<UserModel> = MutableLiveData(UserModel(null, "", ""))
    var actualUser = _actualUser

    private var _email: MutableLiveData<String> = MutableLiveData("")

    private var _password: MutableLiveData<String> = MutableLiveData("")

    fun getUsers() {
        repository.getUsers().addSnapshotListener { value, error ->
            if (error != null) {
                Log.e("Firebase error", error.message.toString())
                return@addSnapshotListener
            }
            val tempList = mutableListOf<UserModel>()
            for (dc: DocumentChange in value?.documentChanges!!) {
                if (dc.type == DocumentChange.Type.ADDED) {
                    val newUser = dc.document.toObject(UserModel::class.java)
                    newUser.userId = dc.document.id
                    tempList.add(newUser)
                }
            }
            _userList.value = tempList
        }
    }

    fun getUser(userId: String) {
        repository.getUser(userId).addSnapshotListener { value, error ->
            if (error != null) {
                Log.w("UserRepository", "Listen Failed", error)
                return@addSnapshotListener
            }
            if (value != null && value.exists()) {
                val user = value.toObject(UserModel::class.java)
                if (user != null) {
                    user.userId = userId
                }
                _actualUser.value = user
                _email.value = _actualUser.value!!.email
                _password.value = _actualUser.value!!.password
            } else {
                Log.d("UserRepository", "Current dat: null")
            }
        }
    }









    // Firebase authentication
    private var auth = FirebaseAuth.getInstance()

    private var _userId: MutableLiveData<String> = MutableLiveData("")
    private var _loggedUser: MutableLiveData<String> = MutableLiveData("")
    private var _goToNext: MutableLiveData<Boolean> = MutableLiveData(false)
    private var _showCircularProgressBar: MutableLiveData<Boolean> = MutableLiveData(false)

    fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _goToNext.value = true
                } else {
                    _goToNext.value = false
                    Log.d("Error", "Error creating user: ${task.result}")
                }
                modifyProcessing()
            }
    }

    fun login(email: String?, password: String?) {
        auth.signInWithEmailAndPassword(email!!, password!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _userId.value = task.result.user?.uid
                    _loggedUser.value = task.result.user?.email?.split("@")?.get(0)
                    _goToNext.value = true
                } else {
                    _goToNext.value = false
                    Log.d("Error", "Error signing in: ${task.result}")
                }
                modifyProcessing()
            }
    }

    fun logout() {
        auth.signOut()
    }

    private fun modifyProcessing() {
        _showCircularProgressBar.value = _showCircularProgressBar.value != true
    }

}