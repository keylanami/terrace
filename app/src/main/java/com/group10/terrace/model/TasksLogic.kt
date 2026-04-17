package com.group10.terrace.model

data class TasksLogic(
    val recurringTask: List<RecurringTask> = emptyList(),
    val milestoneTask: List<MilestoneTask> = emptyList()
)

interface TaskItem {
    val task_name: String
}

data class RecurringTask(
    override val task_name: String = "",
    val frequency_days: Int = 1,
    val points: Int = 0
) : TaskItem

data class MilestoneTask(
    val day: Int = 0,
    override val task_name: String = "",
    val points: Int = 0
) : TaskItem