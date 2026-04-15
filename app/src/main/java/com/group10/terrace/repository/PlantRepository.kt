package com.group10.terrace.repository

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
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

    fun getRecommendedPlants(landSize: Double): List<Plant> {
        val allPlants = getMasterPlants()
        return if (landSize < 2.0) {
            allPlants.filter { it.difficulty == "Mudah" || it.type == "Sayur" }
        } else {
            allPlants
        }
    }



    fun startPlanting(userId: String, plant: Plant, onResult: (Boolean) -> Unit) {
        val userPlantId = db.collection("users").document(userId).collection("active_plants").document().id

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