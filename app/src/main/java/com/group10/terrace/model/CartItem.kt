package com.group10.terrace.model

data class CartItem(
    val cartItemId: String = "",
    val productId: String = "",
    val productName: String = "",
    val price: Double = 0.0,
    val quantity: Int = 1,
    val maxStock: Int = 0,
    val imageUrl: String = ""
)
