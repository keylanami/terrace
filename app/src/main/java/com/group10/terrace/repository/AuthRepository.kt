package com.group10.terrace.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.group10.terrace.model.User

class AuthRepository {

    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore


    fun register(email: String, pass: String, name: String, landSize: Double, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = task.result?.user?.uid ?: return@addOnCompleteListener
                    saveUserToFirestore(uid, email, name, landSize, onResult)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }


    private fun saveUserToFirestore(uid: String, email: String, name: String, landSize: Double, onResult: (Boolean, String?) -> Unit) {
        val newUser = User(
            uid = uid,
            name = name,
            email = email,
            landSize = landSize,
            totalPoints = 0,
            currentPoint = 0,
            currentStreak = 0
        )

        db.collection("users").document(uid).set(newUser)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }



    fun login(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }



    fun getCurrentUser(onResult: (User?) -> Unit) {
        val currentUserUid = auth.currentUser?.uid
        if (currentUserUid != null) {
            db.collection("users").document(currentUserUid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val user = document.toObject(User::class.java)
                        onResult(user)
                    } else {
                        onResult(null)
                    }
                }
                .addOnFailureListener {
                    onResult(null)
                }
        } else {
            onResult(null)
        }
    }

    fun logout() {
        auth.signOut()
    }
}