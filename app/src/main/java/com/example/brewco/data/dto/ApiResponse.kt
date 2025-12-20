package com.example.brewco.data.dto

import androidx.annotation.Keep


@Keep
data class ApiResponse<T>(
    val error: Boolean,
    val statusCode: Int,
    val data: T,
    val message: String
)
