package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    
    private val _loggedInUsername = MutableStateFlow<String?>(prefs.getString("username", null))
    val loggedInUsername: StateFlow<String?> = _loggedInUsername

    fun saveSession(username: String) {
        prefs.edit().putString("username", username).apply()
        _loggedInUsername.value = username
    }

    fun clearSession() {
        prefs.edit().remove("username").apply()
        _loggedInUsername.value = null
    }
}
