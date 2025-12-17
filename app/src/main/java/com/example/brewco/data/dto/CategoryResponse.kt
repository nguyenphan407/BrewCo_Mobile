package com.example.brewco.data.dto

import androidx.annotation.Keep

@Keep
data class CategoryResponse(
    val id: Int,
    val name: String,
    val description: String
)
