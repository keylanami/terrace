package com.group10.terrace.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.group10.terrace.model.User
import java.util.Calendar

class GamificationRepository {

    private val db: FirebaseFirestore = Firebase.firestore

    fun completeMissionAndUpdateStats(
        userId: String,
        difficulty: String,
        onResult: (Boolean, Int, Int) -> Unit
    ) {
        val userRef = db.collection("users").document(userId)

        userRef.get().addOnSuccessListener { document ->
            val user = document.toObject(User::class.java) ?: return@addOnSuccessListener

            val pointsEarned = when (difficulty) {
                "Mudah" -> 10
                "Sedang" -> 20
                "Sulit" -> 35
                else -> 10
            }
            val newTotalPoints = user.totalPoints + pointsEarned
            val newCurrentPoints = user.currentPoint + pointsEarned

            val newStreak = calculateStreak(user.lastActiveDays, user.currentStreak)

            userRef.update(
                mapOf(
                    "totalPoints" to newTotalPoints,
                    "currentPoints" to newCurrentPoints,
                    "currentStreak" to newStreak,
                    "lastActiveDate" to System.currentTimeMillis()
                )
            ).addOnSuccessListener {
                Log.d("TERRACE_GAME", "Misi Selesai! +$pointsEarned Poin. Streak: $newStreak")
                onResult(true, newTotalPoints, newStreak)
            }.addOnFailureListener {
                onResult(false, user.totalPoints, user.currentStreak)
            }
        }.addOnFailureListener {
            onResult(false, 0, 0)
        }
    }


    private fun calculateStreak(lastActiveMillis: Long, currentStreak: Int): Int {
        val today = Calendar.getInstance()
        val lastActive = Calendar.getInstance().apply { timeInMillis = lastActiveMillis }

        val dayDifference = today.get(Calendar.DAY_OF_YEAR) - lastActive.get(Calendar.DAY_OF_YEAR)
        val yearDifference = today.get(Calendar.YEAR) - lastActive.get(Calendar.YEAR)

        return if (yearDifference == 0 && dayDifference == 0) {
            currentStreak
        } else if ((yearDifference == 0 && dayDifference == 1) || (yearDifference == 1 && today.get(Calendar.DAY_OF_YEAR) == 1)) {
            currentStreak + 1
        } else {
            1
        }
    }
}