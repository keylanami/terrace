package com.group10.terrace.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val profileImageUrl: String = "",

    val landSize: Double = 0.0,
    val location: String = "",
    val experience: String = "",

    val badges: List<String> = emptyList(),
    val lastActiveDays: Long = System.currentTimeMillis(),
    val environment: String = "",
    val totalPoints: Int = 0,
    val currentStreak: Int = 0,
    val currentPoint: Int = 0,
    val totalHarvested: Int = 0,

    val address: String = "",
    val phoneNumber: String = "",

    val addresses: List<UserAddress> = emptyList()
)