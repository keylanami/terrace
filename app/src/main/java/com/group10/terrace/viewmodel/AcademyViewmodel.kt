package com.group10.terrace.viewmodel

import androidx.lifecycle.ViewModel
import com.group10.terrace.model.EducationContent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AcademyViewmodel: ViewModel(){

    private val _contents = MutableStateFlow<List<EducationContent>>(emptyList())

    val contents: StateFlow<List<EducationContent>> = _contents

    init {
        _contents.value = listOf(
            EducationContent("E01", "Cara Menyemai Benih Tomat", "Video", false),
            EducationContent("E02", "5 Tanaman Cocok di 1m2", "Article", false),
            EducationContent("E03", "Rahasia Pupuk Organik Cair", "Video", true, 500),
            EducationContent("E04", "Merawat Monstera Variegata", "Article", true, 1000)
        )
    }

    fun isContentUnlocked(userTotalPoints: Int, content: EducationContent): Boolean {
        if (!content.isPremium) return true
        return userTotalPoints >= content.requiredPoints
    }
}