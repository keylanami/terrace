package com.group10.terrace.model

data class EducationContent(
    val id: String,
    val title: String,
    val type: String,
    val isPremium: Boolean,
    val requiredPoints: Int = 0
)
