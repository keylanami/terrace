package com.group10.terrace.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.group10.terrace.model.*
import java.util.Calendar

class GamificationRepository {

    private val db: FirebaseFirestore = Firebase.firestore

    fun getTodayMissions(userPlant: UserPlant, plantMaster: Plant): List<Mission> {
        val todayMissions = mutableListOf<Mission>()
        val todayMillis = System.currentTimeMillis()

        val plantingAgeInDays = calculateDaysBetween(userPlant.startDate, todayMillis)
        val currentDay = if (plantingAgeInDays == 0L) 1 else plantingAgeInDays.toInt()

        val isSameDay = isSameDay(userPlant.lastTaskCompletionDate, todayMillis)
        val completedToday = if (isSameDay) userPlant.completedTaskToday else emptyList()

        val logic = plantMaster.tasks_logic ?: return emptyList()

        logic.recurringTask.forEach { task ->
            if (currentDay % task.frequency_days == 0) {
                todayMissions.add(
                    Mission(
                        name = task.task_name,
                        points = task.points,
                        isMilestone = false,
                        isCompleted = completedToday.contains(task.task_name)
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
                        isMilestone = true,
                        isCompleted = completedToday.contains(milestone.task_name)
                    )
                )
            }
        }

        return todayMissions
    }

    fun completeMissionAndUpdateStats(
        userId: String,
        userPlantId: String,
        mission: Mission,
        onResult: (Boolean, Int, Int) -> Unit
    ) {
        val userRef = db.collection("users").document(userId)
        val plantRef = userRef.collection("active_plants").document(userPlantId)

        var updatedTotalPoints = 0
        var updatedStreak = 0

        db.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val user = snapshot.toObject(User::class.java) ?: return@runTransaction

            val pointsEarned = mission.points

            updatedTotalPoints = user.totalPoints + pointsEarned
            val newCurrentPoints = user.currentPoint + pointsEarned
            updatedStreak = calculateStreak(user.lastActiveDays, user.currentStreak)

            transaction.update(userRef, "totalPoints", updatedTotalPoints)
            transaction.update(userRef, "currentPoint", newCurrentPoints)
            transaction.update(userRef, "currentStreak", updatedStreak)
            transaction.update(userRef, "lastActiveDays", System.currentTimeMillis())

            transaction.update(plantRef, "completedTaskToday", FieldValue.arrayUnion(mission.name))
            transaction.update(plantRef, "lastTaskCompletionDate", System.currentTimeMillis())

        }.addOnSuccessListener {
            Log.d("TERRACE_GAME", "Misi Selesai! +${mission.points} Poin. Streak: $updatedStreak")
            onResult(true, updatedTotalPoints, updatedStreak)
        }.addOnFailureListener { e ->
            Log.e("TERRACE_GAME", "Gagal menyimpan misi: ${e.message}")
            onResult(false, 0, 0)
        }
    }

    private fun calculateDaysBetween(startDateMillis: Long, endDateMillis: Long): Long {
        val diffMillis = endDateMillis - startDateMillis
        return diffMillis / (1000 * 60 * 60 * 24)
    }

    private fun isSameDay(millis1: Long, millis2: Long): Boolean {
        if (millis1 == 0L) return false
        val cal1 = Calendar.getInstance().apply { timeInMillis = millis1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = millis2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun getMidnight(timeInMillis: Long): Calendar {
        return Calendar.getInstance().apply {
            this.timeInMillis = timeInMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    private fun calculateStreak(lastActiveMillis: Long, currentStreak: Int): Int {
        if (lastActiveMillis == 0L) return 1

        val todayMidnight = getMidnight(System.currentTimeMillis())
        val lastActiveMidnight = getMidnight(lastActiveMillis)

        val diffMillis = todayMidnight.timeInMillis - lastActiveMidnight.timeInMillis
        val diffDays = (diffMillis / (1000 * 60 * 60 * 24)).toInt()

        return when {
            diffDays == 0 -> if (currentStreak == 0) 1 else currentStreak
            diffDays == 1 -> currentStreak + 1
            else -> 1
        }
    }
}