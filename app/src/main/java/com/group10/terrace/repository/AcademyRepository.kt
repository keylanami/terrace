package com.group10.terrace.repository

import android.content.Context
import com.google.gson.Gson
import com.group10.terrace.model.AcademyResponse
import java.io.IOException

class AcademyRepository(private val context: Context) {

    fun getAcademyData(): AcademyResponse? {
        return try {
            val jsonString = context.assets.open("education_content.json").bufferedReader().use { it.readText() }
            Gson().fromJson(jsonString, AcademyResponse::class.java)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}