package com.group10.terrace.model

data class Order(
    val orderId: String = "",
    val items: List<CartItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val status: String = "Menunggu Pembayaran", // nunggu, dikemas, dikirim, selesai
    val orderDate: Long = System.currentTimeMillis()

)
