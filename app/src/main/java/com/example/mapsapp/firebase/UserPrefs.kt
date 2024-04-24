package com.example.mapsapp.firebase

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class UserPrefs(private val context: Context) {

    // Create a datastore
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")
        val STORE_USERMAIL = stringPreferencesKey("store_username")
        val STORE_USERPASS = stringPreferencesKey("store_userpass")
    }

    // Get the user data
    val getUserData: Flow<List<String>> = context.dataStore.data.map { prefs ->
        listOf(
            prefs[STORE_USERMAIL] ?: "",
            prefs[STORE_USERPASS] ?: ""
        )
    }

    // Save the user data
    suspend fun saveUserData(email: String, userpass: String) {
        context.dataStore.edit { prefs ->
            prefs[STORE_USERMAIL] = email
            prefs[STORE_USERPASS] = userpass
        }
        Log.d("UserPrefs", "Saved user data: Mail=${email}, Pass=${userpass}")
    }

    // Delete the user data
    fun deleteUserData(context: Context) {
        runBlocking {
            context.dataStore.edit { prefs ->
                prefs[STORE_USERMAIL] = ""
                prefs[STORE_USERPASS] = ""
            }
        }
    }
}
