package com.group10.terrace.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.group10.terrace.model.Plant
import com.group10.terrace.model.User
import com.group10.terrace.model.UserPlant
import com.group10.terrace.repository.AuthRepository
import com.group10.terrace.repository.GamificationRepository
import com.group10.terrace.repository.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.TimeUnit

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepo = AuthRepository()
    private val plantRepo = PlantRepository(application.applicationContext)
    private val gamificationRepo = GamificationRepository() // Tambahkan Repo Gamifikasi

    private val _userData = MutableStateFlow<User?>(null)
    val userData: StateFlow<User?> = _userData

    private val _activePlants = MutableStateFlow<List<UserPlant>>(emptyList())
    val activePlants: StateFlow<List<UserPlant>> = _activePlants

    private val _recommendations = MutableStateFlow<List<Plant>>(emptyList())
    val recommendations: StateFlow<List<Plant>> = _recommendations

    private val _masterPlants = MutableStateFlow<List<Plant>>(emptyList())
    val masterPlants: StateFlow<List<Plant>> = _masterPlants

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        authRepo.getCurrentUser { user ->
            _userData.value = user
            user?.let {

                // --- EKSEKUSI BUG 1: Cek & Reset Streak Otomatis ---
                gamificationRepo.checkAndResetStreak(it.uid, it.lastActiveDays, it.currentStreak)

                val masterList = plantRepo.getMasterPlants()
                _masterPlants.value = masterList
                _recommendations.value = plantRepo.getRecommendedPlants(it.landSize, it.experience)

                plantRepo.getActivePlants(it.uid) { plants ->
                    val updatedPlants = plants.map { userPlant ->
                        val master = masterList.find { m -> m.id == userPlant.plantId }
                        val maxDays = extractMaxDaysVM(master?.harvest_duration ?: "30 hari")
                        val currentDay = calculateDaysPassedVM(userPlant.startDate).toInt()

                        // Hitung Progress
                        val rawProgress = if (maxDays > 0) currentDay.toFloat().coerceAtMost(maxDays.toFloat()) / maxDays.toFloat() else 0f
                        val progressPercentage = (rawProgress.coerceIn(0f, 1f) * 100).toInt()

                        // --- EKSEKUSI BUG 3: Kalkulasi Kesehatan Tanaman ---
                        // Cari hari terakhir dirawat. Jika kosong (baru nanam), anggap dirawat hari 1.
                        val lastTaskDay = userPlant.taskHistory.maxOrNull() ?: 1
                        val missedDays = currentDay - lastTaskDay

                        val newHealth = when {
                            missedDays > 4 -> "Layu"
                            missedDays > 2 -> "Kering"
                            else -> "Subur"
                        }

                        // Kalau berubah, update Firestore di latar belakang
                        if (newHealth != userPlant.healthStatus) {
                            Firebase.firestore.collection("users").document(it.uid)
                                .collection("active_plants").document(userPlant.userPlantId)
                                .update("healthStatus", newHealth)
                        }

                        userPlant.copy(progress = progressPercentage, healthStatus = newHealth)
                    }

                    _activePlants.value = updatedPlants
                }
            }
        }
    }

    fun addNewPlant(plant: Plant) {
        val userId = _userData.value?.uid ?: return
        plantRepo.startPlanting(userId, plant) { success ->
            if (success) loadDashboardData()
        }
    }

    private fun extractMaxDaysVM(duration: String): Int {
        val numbers = Regex("\\d+").findAll(duration).map { it.value.toInt() }.toList()
        return numbers.maxOrNull() ?: 30
    }

    private fun calculateDaysPassedVM(startDateMillis: Long): Long {
        val diff = System.currentTimeMillis() - startDateMillis
        return TimeUnit.MILLISECONDS.toDays(diff).coerceAtLeast(1L)
    }
}