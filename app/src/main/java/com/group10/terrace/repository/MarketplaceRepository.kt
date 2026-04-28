package com.group10.terrace.repository

import android.content.Context
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.gson.Gson
import com.group10.terrace.model.CartItem
import com.group10.terrace.model.Order
import com.group10.terrace.model.Product
import com.group10.terrace.model.ProductResponse
import com.group10.terrace.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

class MarketplaceRepository {

    private val db: FirebaseFirestore = Firebase.firestore

    fun getProducts(onResult: (List<Product>) -> Unit) {
        db.collection("products").get()
            .addOnSuccessListener { snapshot ->
                val products = snapshot.toObjects(Product::class.java)
                onResult(products)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun addOrUpdateCart(
        userId: String,
        product: Product,
        quantity: Int,
        onResult: (Boolean) -> Unit
    ) {
        val cartRef =
            db.collection("users").document(userId).collection("cart").document(product.id)

        val cartItem = CartItem(
            cartItemId = product.id,
            productId = product.id,
            productName = product.name,
            price = product.price,
            quantity = quantity,
            maxStock = product.stock,
            imageUrl = product.imageUrl
        )

        cartRef.set(cartItem)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun removeFromCart(userId: String, cartItemId: String, onResult: (Boolean) -> Unit) {
        db.collection("users").document(userId).collection("cart").document(cartItemId)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getCartItems(userId: String, onResult: (List<CartItem>) -> Unit) {
        db.collection("users").document(userId).collection("cart")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }
                val items = value?.toObjects(CartItem::class.java) ?: emptyList()
                onResult(items)
            }
    }

    suspend fun processCheckoutSimulator(
        userId: String,
        items: List<CartItem>,
        total: Double
    ): Result<String> {
        return try {
            kotlinx.coroutines.delay(2000)

            val newOrderRef = db.collection("users").document(userId)
                .collection("orders").document()

            val newOrder = Order(
                orderId = newOrderRef.id,
                items = items,
                totalAmount = total,
                status = "Dikirim",
                orderDate = System.currentTimeMillis()
            )

            val batch = db.batch()

            batch.set(newOrderRef, newOrder)

            val cartRef = db.collection("users").document(userId).collection("cart")
            items.forEach { item ->
                val itemRef = cartRef.document(item.cartItemId)
                batch.delete(itemRef)
            }

            batch.commit().await()

            Result.success("Pesanan berhasil dibuat!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getOrderHistory(userId: String, onResult: (List<Order>) -> Unit) {
        db.collection("users").document(userId).collection("orders")
            .orderBy("orderDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }
                val orders = snapshot.toObjects(Order::class.java)
                onResult(orders)
            }
    }

    fun completeOrder(userId: String, orderId: String, onResult: (Boolean) -> Unit) {
        db.collection("users").document(userId).collection("orders").document(orderId)
            .update("status", "Selesai")
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun uploadKatalogKeFirestore(context: Context) {
        try {
            val jsonString =
                context.assets.open("marketplace.json").bufferedReader().use { it.readText() }
            val response = Gson().fromJson(jsonString, ProductResponse::class.java)
            response.products.forEach { product ->
                db.collection("products").document(product.id).set(product)
                    .addOnSuccessListener { Log.d("TERRACE_SEEDER", "Upload: ${product.name}") }
                    .addOnFailureListener { e -> Log.e("TERRACE_SEEDER", "Gagal: ${e.message}") }
            }
        } catch (e: Exception) {
            Log.e("TERRACE_SEEDER", "Error: ${e.message}")
        }
    }
}