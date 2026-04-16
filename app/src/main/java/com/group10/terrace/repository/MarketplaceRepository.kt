package com.group10.terrace.repository

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.group10.terrace.model.Product
import com.group10.terrace.model.User

class MarketplaceRepository {

    private val db: FirebaseFirestore = Firebase.firestore

    fun getProducts(): List<Product> {
        return listOf(
            Product("ITM01", "Bibit Bayam Unggul", "Bibit", 50.0, "Bibit cepat panen.", 100, "url_image"),
            Product("ITM02", "Pupuk Organik Cair", "Pupuk", 150.0, "Nutrisi alami.", 50, "url_image"),
            Product("ITM03", "Pot Tanah Liat 20cm", "Alat", 300.0, "Estetik untuk teras.", 20, "url_image")
        )
    }


    fun purchaseProduct(userId: String, product: Product, onResult: (Boolean, String?) -> Unit) {
        val userRef = db.collection("users").document(userId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val user = snapshot.toObject(User::class.java) ?: return@runTransaction

            if (user.currentPoint >= product.price) {
                val newPoints = user.currentPoint - product.price.toInt()

                transaction.update(userRef, "currentPoints", newPoints)

                val txRef = db.collection("users").document(userId).collection("transactions").document()
                val txData = mapOf(
                    "productId" to product.id,
                    "productName" to product.name,
                    "amountSpent" to product.price,
                    "timestamp" to System.currentTimeMillis()
                )
                transaction.set(txRef, txData)
            } else {
                throw Exception("Saldo poin tidak mencukupi!")
            }
        }.addOnSuccessListener {
            onResult(true, null)
        }.addOnFailureListener { e ->
            onResult(false, e.message)
        }
    }
}