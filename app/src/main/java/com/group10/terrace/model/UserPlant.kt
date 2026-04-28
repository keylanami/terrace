package com.group10.terrace.model

data class UserPlant(
    val userPlantId: String = "",
    val plantId: String = "",
    val plantName: String = "",
    val progressImageUrl: String = "",
    val progress: Int = 0,
    val status: String = "Growing",
    val lastTaskCompletionDate: Long = 0L,
    val completedTaskToday: List<String> =emptyList(),
    val startDate: Long = System.currentTimeMillis(),

    val taskHistory: List<Int> = emptyList(),
    val healthStatus: String = "Subur"
)
