package com.example.brewco.data.dto

import androidx.annotation.Keep

@Keep
data class UpdateOrderStatusRequest(
    val status: Int
)
