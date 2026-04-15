package com.group10.terrace.model

data class UserPlant(
    val userPlantId: String = "",
    val plantId: String = "",
    val plantName: String = "",
    val progressImageUrl: String = "",
    val progress: Int = 0,
    val status: String = "Growing",
    val dailyMission: List<Mission> = listOf(
        Mission(name = "Menyiram Tanaman"),
        Mission(name = "Memberi Pupuk"),
        Mission(name = "Cek Kondisi")
    ),

    val isWateredToday: Boolean = false,
    val isFertilizedToday: Boolean = false,
    val startDate: Long = System.currentTimeMillis()
)
