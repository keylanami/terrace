package com.group10.terrace.repository

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.group10.terrace.model.Plant
import com.group10.terrace.model.UserPlant
import java.io.IOException

class PlantRepository(private val context: Context) {
    private val db: FirebaseFirestore = Firebase.firestore

    fun getMasterPlants(): List<Plant> {
        return try {
            val jsonString = context.assets.open("plants.json").bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<Plant>>() {}.type
            Gson().fromJson(jsonString, listType)
        } catch (e: IOException) {
            emptyList()
        }
    }

    fun getRecommendedPlants(landSize: Double, experience: String): List<Plant> {
        val allPlants = getMasterPlants()

        val filteredBySkill = when (experience) {
            "Pemula (Newbie)" -> allPlants.filter {
                it.difficulty.lowercase() in listOf("easy", "mudah")
            }
            "Menengah (Amateur)" -> allPlants.filter {
                it.difficulty.lowercase() in listOf("easy", "mudah", "medium", "sedang")
            }
            else -> allPlants
        }


        return if (landSize < 2.0) {
            filteredBySkill.filter { it.area_per_pot_m2 <= landSize || it.area_per_pot_m2 <= 0.3 }
        } else {
            filteredBySkill
        }
    }

    fun startPlanting(userId: String, plant: Plant, onResult: (Boolean) -> Unit) {
        val userPlantId = db.collection("users").document(userId)
            .collection("active_plants").document().id

        val newActivePlant = UserPlant(
            userPlantId = userPlantId,
            plantId = plant.id,
            plantName = plant.name,
            startDate = System.currentTimeMillis(),
            progress = 0,
            status = "Growing"
        )

        db.collection("users").document(userId)
            .collection("active_plants")
            .document(userPlantId)
            .set(newActivePlant)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getActivePlants(userId: String, onResult: (List<UserPlant>) -> Unit) {
        db.collection("users").document(userId)
            .collection("active_plants")
            .whereEqualTo("status", "Growing")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }
                val plants = value?.toObjects(UserPlant::class.java) ?: emptyList()
                onResult(plants)
            }
    }
}