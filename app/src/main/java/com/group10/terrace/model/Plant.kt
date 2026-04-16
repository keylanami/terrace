package com.group10.terrace.model

import android.R

data class Plant(
    val id: String = "",
    val name: String = "",
    val type: String = "",
    val category: String = "",
    val difficulty: String = "",
    val estimationDays: Int = 0,
    val description: String = "",
    val careRequirements: String = "",
    val area_per_pot_m2: Double = 0.0,
    val harvest_duration: String = "",
    val startingSteps: List<String> = emptyList(),
    val isPremium: Boolean = false,
    val tasks_logic: TasksLogic? = null,
    val imageUrl: String = ""
)
