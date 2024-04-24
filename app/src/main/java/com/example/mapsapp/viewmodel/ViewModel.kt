package com.example.mapsapp.viewmodel

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mapsapp.R
import com.example.mapsapp.firebase.Repo
import com.example.mapsapp.model.MarkerInfo
import com.example.mapsapp.model.UserModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.storage.FirebaseStorage
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ViewModel: ViewModel() {
    val myColor1 = Color(android.graphics.Color.parseColor("#222222"))
    val myColor2 = Color(android.graphics.Color.parseColor("#1C5D99"))
    val myFontFamily = FontFamily(Font(R.font.rubik))

    var deviceLatLng: MutableLiveData<LatLng> = MutableLiveData(LatLng(0.0, 0.0))
    var lastKnownLocation: MutableLiveData<LatLng>? = null
    var comingFromList: MutableLiveData<Boolean> = MutableLiveData(false)

    private var _currentMarker: MarkerInfo = MarkerInfo("ITB", latitude = 41.4534265, longitude = 2.1837151, "itb", null, "")
    var currentMarker = _currentMarker

    private val _filterType = MutableLiveData<String>("")
    var filterType = _filterType
    private val _filteredMarkers = MutableLiveData<MutableList<MarkerInfo>>(mutableListOf())
    var filteredMarkers = _filteredMarkers

    private val _fotos = MutableLiveData<MutableList<String>>(mutableListOf())
    val fotos = _fotos

    private val _photosInTransit = MutableLiveData<MutableList<String>>(mutableListOf())
    var photosInTransit = _photosInTransit

    private var _takePhotoFromCreateMarker = MutableLiveData(false)
    var takePhotoFromCreateMarker = _takePhotoFromCreateMarker

    private var _isPopupVisible = MutableLiveData(false)
    var isPopupVisible = _isPopupVisible


    var showOverlay = MutableLiveData(false)


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

    fun addPhoto(photo: String, markerInfo: MarkerInfo) {
        val currentList = markerInfo.photos
        currentList?.add(photo)
    }

    fun removeMarker(marker: MarkerInfo) {
        val currentList = _markerList.value?.toMutableList()
        currentList?.remove(marker)
        _markerList.value = currentList
        repository.deleteMarker(marker.markerId) // Borra en Firebase
    }

    fun changePopUpVisibility(value: Boolean) {
        _isPopupVisible.value = value
    }

    fun changeTakePhotoFromCreateMarker(value: Boolean) {
        _takePhotoFromCreateMarker.value = value
    }

    fun addImageToTransit(image: String) {
        _photosInTransit.value?.add(image)
    }



    // Firebase Firestore
    private val repository = Repo()

    private var _markerList: MutableLiveData<MutableList<MarkerInfo>> = MutableLiveData(mutableListOf())
    var markerList = _markerList

    private var _actualMarker: MutableLiveData<MarkerInfo> = MutableLiveData(MarkerInfo("", latitude = 0.0, longitude = 0.0, "Type", null, ""))
    var actualMarker = _actualMarker

//    private var _email: MutableLiveData<String> = MutableLiveData("")
//
//    private var _password: MutableLiveData<String> = MutableLiveData("")
//

    fun addMarker(marker: MarkerInfo) {
        val currentMarkers = _markerList.value ?: mutableListOf()
        currentMarkers.add(marker)
        _markerList.value = currentMarkers
        repository.addMarker(marker) // Guarda en Firebase
    }

    fun editMarker(marker: MarkerInfo) {
        repository.editMarker(marker) // Edita en Firebase
    }

    fun getMarkers(userId: String) {
        repository.getMarkers()
            .whereEqualTo("userId", userId)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("Firebase error", error.message.toString())
                    return@addSnapshotListener
                }
                val tempList = mutableListOf<MarkerInfo>()
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val newMarker = dc.document.toObject(MarkerInfo::class.java)
                        newMarker.userId = dc.document.id
                        tempList.add(newMarker)
                    }
                }
                _markerList.value = tempList
            }
    }

    fun getMarkersOfAType(userId: String, type: String, callback: (List<MarkerInfo>) -> Unit) {
        val tempList = mutableListOf<MarkerInfo>()
        repository.getMarkers()
            .whereEqualTo("userId", userId)
            .whereEqualTo("type", type)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("Firebase error", error.message.toString())
                    callback(emptyList()) // Llama a la devolución de llamada con una lista vacía en caso de error
                    return@addSnapshotListener
                }
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        val newMarker = dc.document.toObject(MarkerInfo::class.java)
                        newMarker.userId = dc.document.id
                        tempList.add(newMarker)
                    }
                }
                callback(tempList) // Llama a la devolución de llamada con la lista actualizada
            }
    }



    fun getMarker(markerId: String) {
        repository.getMarker(markerId).addSnapshotListener { value, error ->
            if (error != null) {
                Log.w("UserRepository", "Listen Failed", error)
                return@addSnapshotListener
            }
            if (value != null && value.exists()) {
                val marker = value.toObject(MarkerInfo::class.java)
                if (marker != null) {
                    marker.markerId = markerId
                }
                _actualMarker.value = marker
            } else {
                Log.d("UserRepository", "Current dat: null")
            }
        }
    }









    // Firebase Authentication
    private var auth = FirebaseAuth.getInstance()
    fun getAuth(): FirebaseAuth {
        return auth
    }

    private var _userId: MutableLiveData<String> = MutableLiveData("")
    var userId = _userId
    private var _actualUser: MutableLiveData<UserModel> = MutableLiveData(UserModel("", "", ""))
    var actualUser = _actualUser
    private var _actualUserName: MutableLiveData<String> = MutableLiveData("")
    var actualUserName = _actualUserName
    private var _goToNext: MutableLiveData<Boolean> = MutableLiveData(false)
    var goToNext = _goToNext
    private var _showCircularProgressBar: MutableLiveData<Boolean> = MutableLiveData(false)
    var showCircularProgressBar = _showCircularProgressBar

    private var _registerFail: MutableLiveData<Boolean> = MutableLiveData(false)
    var registerFail = _registerFail
    private var _loginFail: MutableLiveData<Boolean> = MutableLiveData(false)
    var loginFail = _loginFail

    private var _registering: MutableLiveData<Boolean> = MutableLiveData(false)
    var registering = _registering

    fun register(email: String?, password: String?) {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            auth.createUserWithEmailAndPassword(email ?: "", password ?: "")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _goToNext.value = true
                        modifyProcessing()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Error", "Authentication failed", exception)
                    _registerFail.value = true
                    modifyProcessing()
                }
        } else {
            // Handle invalid email format
            Log.d("Error", "Invalid email format")
        }
    }

    fun login(email: String?, password: String?) {
        if (email.isNullOrBlank() || password.isNullOrBlank()) {
            Log.d("Error", "Empty email or password")
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _userId.value = task.result?.user?.uid
                    if (_actualUserName.value == "") { _actualUserName.value = task.result?.user?.email?.split("@")?.get(0) }
                    _goToNext.value = true
                    //getMarkers(_userId.value ?: "")
                    modifyProcessing()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Error", "Authentication failed", exception)
                _loginFail.value = true
                modifyProcessing()
            }
    }

    fun getUser(userId: String) {
        repository.getUser(userId).addSnapshotListener { value, error ->
            if (error != null) {
                Log.e("UserRepository", "Listen failted", error)
                return@addSnapshotListener
            }
            if (value != null && value.exists()) {
                val user = value.toObject(UserModel::class.java)
                if (user != null) {
                    user.userId = userId
                }
                _actualUser.value = user
                _actualUserName.value = _actualUser.value?.userName
            } else {
                Log.e("UserRepository", "Current data: null")
            }
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun modifyProcessing() {
        _showCircularProgressBar.value = _showCircularProgressBar.value != true
    }








    // Firebase Storage
    fun uploadImage(imageUri: Uri) {
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val storage = FirebaseStorage.getInstance().getReference("images/$fileName")
        storage.putFile(imageUri)
            .addOnSuccessListener {
                Log.i("IMAGE IPLOAD", "Image uploaded successfully")
                storage.downloadUrl.addOnSuccessListener {
                    Log.i("IMAGEN", it.toString())
                }
            }
            .addOnFailureListener {
                Log.i("IMAGE UPLOAD", "Image uploaded failed")
            }
    }



    fun bitmapToUri(context: Context, bitmap: Bitmap): Uri? {
        val filename = "${System.currentTimeMillis()}.jpg"
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, filename)
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
            put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        }

        val uri: Uri? = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        uri?.let {
            val outstream: OutputStream? = context.contentResolver.openOutputStream(it)
            outstream?.let { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
            outstream?.close()
        }

        return uri
    }

}