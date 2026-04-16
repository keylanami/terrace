package com.group10.terrace.model

import com.google.gson.annotations.SerializedName

data class Product(
    val id: String = "",

    @SerializedName("nama")
    val name: String = "",

    val category: String = "",

    val price: Double = 0.0,

    @SerializedName("deskripsi")
    val description: String = "",

    val stock: Int = 0,

    @SerializedName("image_url")
    val imageUrl: String = "",

    @SerializedName("seller_name")
    val sellerName: String = "",

    val rating: Double = 0.0
)