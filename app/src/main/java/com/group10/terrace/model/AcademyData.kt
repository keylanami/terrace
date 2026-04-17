package com.group10.terrace.model

import com.google.gson.annotations.SerializedName

data class AcademyData(
    @SerializedName("artikel")
    val articles: List<EducationItem> = emptyList(),

    @SerializedName("video")
    val videos: List<EducationItem> = emptyList()
)
