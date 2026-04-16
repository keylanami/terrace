package com.group10.terrace.model

import com.google.gson.annotations.SerializedName

data class TasksLogic(


    @SerializedName("recurring")
    val recurringTask: List<RecurringTask> = emptyList(),

    @SerializedName("milestones")
    val milestoneTask: List<MilestoneTask> = emptyList()
)
