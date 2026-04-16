package com.group10.terrace.model

data class RecurringTask(
    val task_name: String = "",
    val frequencyDays: Int = 1,
    val points: Int = 0
)
