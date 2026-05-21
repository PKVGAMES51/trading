package com.example.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppRepository
import com.example.data.SessionManager
import com.example.data.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AppRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _authState.value = AuthState.Error("Username and password required")
            return
        }
        
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val response = com.example.data.RetrofitClient.apiService.login(username, password)
                if (response.success) {
                    val saldoFromApi = response.saldo?.toDoubleOrNull() ?: 0.0
                    val existing = repository.getUserByUsername(username)
                    if (existing == null) {
                        repository.insertUser(com.example.data.UserEntity(username = username, password = "", name = "User", handphone = "", email = "", bank = "", no_rekening = "", saldo = saldoFromApi))
                    } else {
                        repository.updateSaldo(username, saldoFromApi)
                    }
                    sessionManager.saveSession(username)
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error(response.message ?: "Invalid credentials")
                }
            } catch (e: Exception) {
                // Fallback to local DB for testing if needed or just show network error
                _authState.value = AuthState.Error(e.message ?: "Network Error")
            }
        }
    }

    fun register(
        username: String,
        password: String,
        pass2: String,
        name: String,
        handphone: String,
        email: String,
        bank: String,
        noRek: String
    ) {
        if (username.isBlank() || password.isBlank() || name.isBlank() || noRek.isBlank()) {
            _authState.value = AuthState.Error("Semua field wajib diisi")
            return
        }
        if (password != pass2) {
            _authState.value = AuthState.Error("Password tidak cocok")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            try {
                val response = com.example.data.RetrofitClient.apiService.register(
                    username, password, name, handphone, email, bank, noRek
                )
                if (response.success) {
                    repository.insertUser(com.example.data.UserEntity(username = username, password = password, name = name, handphone = handphone, email = email, bank = bank, no_rekening = noRek, saldo = 0.0))
                    sessionManager.saveSession(username)
                    _authState.value = AuthState.Success
                } else {
                    _authState.value = AuthState.Error(response.message ?: "Registrasi gagal")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Network Error")
            }
        }
    }
    
    fun resetState() {
        _authState.value = AuthState.Idle
    }
    
    fun logout() {
        sessionManager.clearSession()
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}
