package com.example.brewco.data.dto

import androidx.annotation.Keep

@Keep
data class CategoryRequest(
    val name: String,
    val description: String
)
