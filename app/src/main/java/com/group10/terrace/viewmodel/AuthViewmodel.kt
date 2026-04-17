package com.group10.terrace.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.group10.terrace.repository.AuthRepository

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    // Parameter landSize dihapus, karena sudah pindah ke layar Personalisasi
    fun registerUser(email: String, pass: String, name: String) {
        Log.d("TERRACE_AUTH", "Memulai proses register...")

        repository.register(email, pass, name) { isSuccess, errorMessage ->
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

    // Fungsi Submit Personalisasi
    fun updatePersonalizationData(userId: String, landSize: Double, location: String, experience: String, onComplete: (Boolean) -> Unit) {
        repository.updatePersonalizationData(userId, landSize, location, experience) { success ->
            if (success) {
                Log.d("TERRACE_AUTH", "Personalisasi SUKSES!")
            } else {
                Log.e("TERRACE_AUTH", "Personalisasi GAGAL!")
            }
            onComplete(success) // Lempar status ke UI untuk pindah ke Dashboard
        }
    }
}