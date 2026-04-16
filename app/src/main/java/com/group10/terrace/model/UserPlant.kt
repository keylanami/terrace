package com.group10.terrace.model

data class UserPlant(
    val userPlantId: String = "",
    val plantId: String = "",
    val plantName: String = "",
    val progressImageUrl: String = "",
    val progress: Int = 0,
    val status: String = "Growing",
    val isWateredToday: Boolean = false,
    val isFertilizedToday: Boolean = false,
    val lastTaskCompletionDate: Long = 0L,
    val startDate: Long = System.currentTimeMillis()
)
