package com.group10.terrace.model

data class Product(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val stock: Int = 0,
    val imageUrl: String = ""
)
