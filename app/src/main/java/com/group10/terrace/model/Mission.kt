package com.group10.terrace.model

data class Mission(
    val name: String = "",
    val points: Int = 0,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val isMilestone: Boolean = false
)
