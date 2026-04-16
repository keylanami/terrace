package com.group10.terrace.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.group10.terrace.model.*
import java.util.Calendar
import kotlin.jvm.java

class GamificationRepository {

    private val db: FirebaseFirestore = Firebase.firestore


    fun getTodayMissions(userPlant: UserPlant, plantMaster: Plant): List<Mission> {
        val todayMissions = mutableListOf<Mission>()

        val plantingAgeInDays = calculateDaysBetween(userPlant.startDate, System.currentTimeMillis())
        val currentDay = if (plantingAgeInDays == 0L) 1 else plantingAgeInDays.toInt()

        val logic = plantMaster.tasks_logic ?: return emptyList()

        logic.recurringTask.forEach { task ->
            if (currentDay % task.frequencyDays == 0) {
                todayMissions.add(
                    Mission(
                        name = task.task_name,
                        points = task.points,
                        isMilestone = false
                    )
                )
            }
        }


        logic.milestoneTask.forEach { milestone ->
            if (currentDay == milestone.day) {
                todayMissions.add(
                    Mission(
                        name = milestone.task_name,
                        points = milestone.points,
                        isMilestone = true
                    )
                )
            }
        }

        return todayMissions
    }

    private fun calculateDaysBetween(startDateMillis: Long, endDateMillis: Long): Long {
        val diffMillis = endDateMillis - startDateMillis
        return diffMillis / (1000 * 60 * 60 * 24)
    }

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