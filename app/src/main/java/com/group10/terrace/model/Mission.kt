package com.group10.terrace.model

data class Mission(
    val name: String = "",
    val isCompleted: Boolean = false,
    val completedAt: Long? = null
)
