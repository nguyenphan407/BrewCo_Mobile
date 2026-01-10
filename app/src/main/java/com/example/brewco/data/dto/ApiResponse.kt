package com.example.brewco.data.dto

data class  ApiResponse<T>(
    val error: Boolean,
    val statusCode: Int,
    val data: T,
    val message: String
)
