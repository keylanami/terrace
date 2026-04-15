package com.group10.terrace.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
// Ganti import di bawah ini jika lokasi Data Class User kamu berbeda
import com.group10.terrace.model.User

class AuthViewModel : ViewModel() {

    // 1. Inisialisasi Firebase Auth dan Firestore (Solusi Unresolved Reference)
    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore

    // 2. Fungsi Register
    fun testRegister() {
        val email = "tester2@terrace.com" // Ganti email jika tester1 sudah terdaftar
        val password = "password123"

        Log.d("TERRACE_TEST", "Memulai proses register...")

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = task.result?.user?.uid
                    Log.d("TERRACE_TEST", "Auth Sukses! UID: $uid")

                    // Jika Auth sukses, otomatis langsung jalankan fungsi Save User
                    if (uid != null) {
                        testSaveUser(uid, email)
                    }
                } else {
                    Log.e("TERRACE_TEST", "Auth Gagal: ${task.exception?.message}")
                }
            }
    }

    // 3. Fungsi Save ke Firestore (Solusi NoSQL Query)
    private fun testSaveUser(uid: String, email: String) {

        // Membentuk struktur data (Pastikan parameter ini sama persis dengan Data Class)
        val userDummy = User(
            uid = uid,
            name = "Keyla Tester",
            email = email,
            landSize = 10.5,
            points = 100,      // Sebelumnya error karena kamu tulis 'points'
            currentPoint = 100,
            currentStreak = 1
        )

        Log.d("TERRACE_TEST", "Mencoba menyimpan data ke Firestore...")

        // Ini adalah cara "Query NoSQL" di Firestore:
        // Masuk ke tabel (collection) "users" -> Buat baris (document) dengan nama UID -> Masukkan data (set)
        db.collection("users").document(uid).set(userDummy)
            .addOnSuccessListener {
                Log.d("TERRACE_TEST", "YEAY! Firestore Berhasil Simpan Data!")
            }
            .addOnFailureListener { e ->
                Log.e("TERRACE_TEST", "WADUH! Firestore Gagal: ${e.message}")
            }
    }
}