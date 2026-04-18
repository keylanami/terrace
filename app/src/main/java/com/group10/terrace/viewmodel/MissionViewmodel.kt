package com.group10.terrace.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.group10.terrace.model.Mission
import com.group10.terrace.model.Plant
import com.group10.terrace.model.UserPlant
import com.group10.terrace.repository.GamificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MissionViewModel : ViewModel() {

    private val gamificationRepo = GamificationRepository()

    private val _todayMissions = MutableStateFlow<List<Mission>>(emptyList())
    val todayMissions: StateFlow<List<Mission>> = _todayMissions

    private val _gamificationEvent = MutableStateFlow<GamificationEvent?>(null)
    val gamificationEvent: StateFlow<GamificationEvent?> = _gamificationEvent

    fun loadTodayMissions(userPlant: UserPlant, plantMaster: Plant) {
        _todayMissions.value = gamificationRepo.getTodayMissions(userPlant, plantMaster)
    }

    fun onTaskChecked(userId: String, userPlantId: String, mission: Mission, plantMaster: Plant) {

        val updatedMissions = _todayMissions.value.map {
            if (it.name == mission.name) it.copy(isCompleted = true) else it
        }
        _todayMissions.value = updatedMissions

        gamificationRepo.completeMissionAndUpdateStats(userId, userPlantId, mission, plantMaster) { success, points, streak ->
            if (success) {
                Log.d("TERRACE_VM", "Berhasil! Total Poin: $points, Streak: $streak")
                _gamificationEvent.value = GamificationEvent.Success(mission.points, streak)
            } else {
                _todayMissions.value = updatedMissions.map {
                    if (it.name == mission.name) it.copy(isCompleted = false) else it
                }
                _gamificationEvent.value = GamificationEvent.Error("Gagal menyimpan progres.")
            }
        }
    }

    fun clearEvent() {
        _gamificationEvent.value = null
    }
}


sealed class GamificationEvent {
    data class Success(val earnedPoints: Int, val newStreak: Int) : GamificationEvent()
    data class Error(val message: String) : GamificationEvent()
}