package com.group10.terrace.viewmodel


import androidx.lifecycle.ViewModel
import com.group10.terrace.repository.GamificationRepository

class MissionViewModel : ViewModel() {

    private val gamificationRepo = GamificationRepository()

    fun onTaskChecked(userId: String, userPlantId: String, plantDifficulty: String) {

        gamificationRepo.completeMissionAndUpdateStats(userId, userPlantId, plantDifficulty) { success, points, streak ->
            if (success) {
                // berhasil!
                // Besok di UI: Kamu bisa memunculkan animasi Lottie koin bertambah
                // atau Toast "Selamat! Poin kamu jadi $points, Streak: $streak api!"
            }
        }
    }

    // Nanti bisa ditambahkan fungsi beli barang di sini
    // (mengurangi currentPoints)

    fun beliBarang(currentPoints: Int){


    }
}