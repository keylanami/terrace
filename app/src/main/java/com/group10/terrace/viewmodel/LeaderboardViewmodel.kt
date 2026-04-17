package com.group10.terrace.viewmodel

import androidx.lifecycle.ViewModel
import com.group10.terrace.model.User
import com.group10.terrace.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LeaderboardViewModel : ViewModel() {

    private val authRepo = AuthRepository()

    private val _leaderboardData = MutableStateFlow<List<User>>(emptyList())
    val leaderboardData: StateFlow<List<User>> = _leaderboardData

    init {
        loadLeaderboard()
    }

    private fun loadLeaderboard() {
        authRepo.getLeaderboard { topUsers ->
            _leaderboardData.value = topUsers
        }
    }
}