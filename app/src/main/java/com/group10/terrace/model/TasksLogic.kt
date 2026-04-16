package com.group10.terrace.model

data class TasksLogic(
    val recurringTask: List<RecurringTask> = emptyList(),
    val milestoneTask: List<MilestoneTask> = emptyList()
)
