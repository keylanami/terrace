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

    fun addOrUpdateCart(userId: String, product: Product, quantity: Int, onResult: (Boolean) -> Unit) {
        val cartRef = db.collection("users").document(userId).collection("cart").document(product.id)

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
        cartItems: List<CartItem>,
        totalPrice: Double
    ): Result<String> {
        return try {
            val userRef = db.collection("users").document(userId)
            val cartCollection = userRef.collection("cart")
            val ordersCollection = userRef.collection("orders")

            Log.d("TERRACE_MARKET", "Memproses pembayaran...")
            delay(3000)

            val batch = db.batch()

            val userSnapshot = userRef.get().await()
            val user = userSnapshot.toObject(User::class.java)
                ?: throw Exception("User tidak ditemukan")

            if (user.currentPoint < totalPrice) {
                throw Exception("Saldo poin tidak mencukupi! Poin: ${user.currentPoint}, Tagihan: $totalPrice")
            }

            val newPoints = user.currentPoint - totalPrice.toInt()
            batch.update(userRef, "currentPoint", newPoints) // 🔴 FIX: "currentPoint" (bukan "currentPoints")

            cartItems.forEach { item ->
                val productRef = db.collection("products").document(item.productId)
                val currentStockSnapshot = productRef.get().await()
                val currentStock = currentStockSnapshot.getLong("stock") ?: 0L
                batch.update(productRef, "stock", currentStock - item.quantity)

                val cartItemRef = cartCollection.document(item.cartItemId)
                batch.delete(cartItemRef)
            }

            val newOrderId = ordersCollection.document().id
            val newOrder = Order(
                orderId = newOrderId,
                items = cartItems,
                totalAmount = totalPrice,
                status = "Dikemas",
                orderDate = System.currentTimeMillis()
            )
            batch.set(ordersCollection.document(newOrderId), newOrder)

            batch.commit().await()

            Log.d("TERRACE_MARKET", "Checkout Berhasil! Order ID: $newOrderId")
            Result.success("Transaksi Berhasil! Pesanan sedang dikemas.")

        } catch (e: Exception) {
            Log.e("TERRACE_MARKET", "Checkout Gagal: ${e.message}")
            Result.failure(e)
        }
    }

    fun getOrderHistory(userId: String, onResult: (List<Order>) -> Unit) {
        db.collection("users").document(userId).collection("orders")
            .orderBy("orderDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }
                val orders = value?.toObjects(Order::class.java) ?: emptyList()
                onResult(orders)
            }
    }

    fun uploadKatalogKeFirestore(context: Context) {
        try {
            val jsonString = context.assets.open("marketplace.json").bufferedReader().use { it.readText() }
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