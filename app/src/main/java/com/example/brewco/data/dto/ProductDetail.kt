package com.example.brewco.data.dto

import androidx.annotation.Keep

@Keep
data class ProductDetail(
    val id: String,
    val name: String,
    val description: String,
    val price: Int,
    val imageUrl: String,
    val categoryId: Int,
    val categoryName: String,
    val createdAt: String,
    val updatedAt: String
)
