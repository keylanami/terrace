package com.group10.terrace.model

import android.R

data class Plant(
    val id: String = "",
    val name: String = "",
    val type: String = "",
    val difficulty: String = "",
    val estimationDays: Int = 0,
    val description: String = "",
    val careRequirements: String = "",
    val startingSteps: List<String> = emptyList(),
    val isPremium: Boolean = false,
    val imageUrl: String = ""
)
