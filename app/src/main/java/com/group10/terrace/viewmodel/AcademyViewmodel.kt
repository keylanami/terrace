package com.group10.terrace.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.group10.terrace.model.EducationItem
import com.group10.terrace.model.User
import com.group10.terrace.repository.AcademyRepository
import com.group10.terrace.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AcademyViewModel(application: Application) : AndroidViewModel(application) {

    private val academyRepo = AcademyRepository(application.applicationContext)
    private val authRepo = AuthRepository()

    private val _articles = MutableStateFlow<List<EducationItem>>(emptyList())
    val articles: StateFlow<List<EducationItem>> = _articles

    private val _videos = MutableStateFlow<List<EducationItem>>(emptyList())
    val videos: StateFlow<List<EducationItem>> = _videos

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _selectedVideo = MutableStateFlow<EducationItem?>(null)
    val selectedVideo: StateFlow<EducationItem?> = _selectedVideo

    private val _selectedArticle = MutableStateFlow<EducationItem?>(null)
    val selectedArticle: StateFlow<EducationItem?> = _selectedArticle

    init {
        loadAcademyData()
    }

    private fun loadAcademyData() {
        authRepo.getCurrentUser { user ->
            _currentUser.value = user
        }

        val response = academyRepo.getAcademyData()
        if (response?.academyContent != null) {
            _articles.value = response.academyContent.articles
            _videos.value = response.academyContent.videos
        }
    }

    fun setSelectedItem(item: EducationItem, type: String) {
        when (type) {
            "Video" -> _selectedVideo.value = item
            "Article" -> _selectedArticle.value = item
        }
    }

    fun refreshUser() {
        authRepo.getCurrentUser { user ->
            _currentUser.value = user
        }
    }
}