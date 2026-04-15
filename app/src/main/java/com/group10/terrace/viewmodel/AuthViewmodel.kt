package com.group10.terrace.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.group10.terrace.repository.AuthRepository

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    fun registerUser(email: String, pass: String, name: String, landSize: String) {
        Log.d("TERRACE_AUTH", "Memulai proses register...")

        val size = landSize.toDoubleOrNull() ?: 0.0

        repository.register(email, pass, name, size) { isSuccess, errorMessage ->
            if (isSuccess) {
                Log.d("TERRACE_AUTH", "Register dan Save Firestore SUKSES!")
            } else {
                Log.e("TERRACE_AUTH", "Register Gagal: $errorMessage")
            }
        }
    }

    fun loginUser(email: String, pass: String) {
        Log.d("TERRACE_AUTH", "Memulai proses login...")

        repository.login(email, pass) { isSuccess, errorMessage ->
            if (isSuccess) {
                Log.d("TERRACE_AUTH", "Login SUKSES!")
            } else {
                Log.e("TERRACE_AUTH", "Login Gagal: $errorMessage")
            }
        }
    }

    fun fetchMe() {
        Log.d("TERRACE_AUTH", "Mencari data user saat ini...")

        repository.getCurrentUser { user ->
            if (user != null) {
                Log.d("TERRACE_AUTH", "Halo, ${user.name}! Poin kamu: ${user.totalPoints}")
            } else {
                Log.d("TERRACE_AUTH", "Tidak ada user yang login / Sesi habis.")

            }
        }
    }
}