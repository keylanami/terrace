package com.group10.terrace.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.group10.terrace.model.Plant
import com.group10.terrace.model.User
import com.group10.terrace.model.UserPlant
import com.group10.terrace.repository.AuthRepository
import com.group10.terrace.repository.PlantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepo = AuthRepository()
    private val plantRepo = PlantRepository(application.applicationContext)

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
                // UPDATE: Lempar juga 'experience' ke repo agar filter sesuai keahlian
                _recommendations.value = plantRepo.getRecommendedPlants(it.landSize, it.experience)
                _masterPlants.value = plantRepo.getMasterPlants()

                plantRepo.getActivePlants(it.uid) { plants ->
                    _activePlants.value = plants
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
}