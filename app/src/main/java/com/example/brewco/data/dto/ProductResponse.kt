package com.example.brewco.data.dto

import androidx.annotation.Keep

@Keep
data class ProductResponse(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val categoryId: Long,
    val categoryName: String
)
