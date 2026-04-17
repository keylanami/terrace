package com.group10.terrace.model

import com.google.gson.annotations.SerializedName

data class AcademyResponse(
    @SerializedName("academy_content")
    val academyContent: AcademyData? = null
)
