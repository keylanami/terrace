package com.group10.terrace.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.group10.terrace.model.User
import com.group10.terrace.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _userData = MutableStateFlow<User?>(null)
    val userData: StateFlow<User?> = _userData

    init {
        fetchCurrentUser()
    }

    fun registerUser(email: String, pass: String, name: String) {
        _authState.value = AuthState.Loading
        repository.register(email, pass, name) { isSuccess, errorMessage ->
            if (isSuccess) {
                Log.d("TERRACE_AUTH", "Register SUKSES!")
                fetchCurrentUser()
                _authState.value = AuthState.Success
            } else {
                Log.e("TERRACE_AUTH", "Register Gagal: $errorMessage")
                _authState.value = AuthState.Error(errorMessage ?: "Register gagal")
            }
        }
    }

    fun loginUser(email: String, pass: String) {
        _authState.value = AuthState.Loading
        repository.login(email, pass) { isSuccess, errorMessage ->
            if (isSuccess) {
                Log.d("TERRACE_AUTH", "Login SUKSES!")
                fetchCurrentUser()
                _authState.value = AuthState.Success
            } else {
                Log.e("TERRACE_AUTH", "Login Gagal: $errorMessage")
                _authState.value = AuthState.Error(errorMessage ?: "Login gagal")
            }
        }
    }

    fun fetchCurrentUser() {
        repository.getCurrentUser { user ->
            _userData.value = user
            if (user != null) {
                Log.d("TERRACE_AUTH", "Halo, ${user.name}!")
            }
        }
    }

    fun logout() {
        repository.logout()
        _userData.value = null
        _authState.value = AuthState.Idle
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    fun updatePersonalizationData(
        userId: String,
        landSize: Double,
        location: String,
        experience: String,
        onComplete: (Boolean) -> Unit
    ) {
        repository.updatePersonalizationData(userId, landSize, location, experience) { success ->
            if (success) {
                fetchCurrentUser()
                Log.d("TERRACE_AUTH", "Personalisasi SUKSES!")
            } else {
                Log.e("TERRACE_AUTH", "Personalisasi GAGAL!")
            }
            onComplete(success)
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}