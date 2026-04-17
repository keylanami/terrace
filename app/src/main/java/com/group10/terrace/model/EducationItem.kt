package com.group10.terrace.model

import com.google.gson.annotations.SerializedName

data class EducationItem(
    val id: String = "",
    val title: String = "",
    val category: String = "",
    val description: String = "",

    val content: String? = null,

    @SerializedName("content_url")
    val contentUrl: String? = null,

    @SerializedName("is_premium")
    val isPremium: Boolean = false,

    @SerializedName("min_points")
    val minPoints: Int = 0,

    @SerializedName("duration_or_time")
    val durationOrTime: String = "",

    @SerializedName("image_url")
    val imageUrl: String = ""
)
