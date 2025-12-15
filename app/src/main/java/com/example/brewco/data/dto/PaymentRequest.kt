package com.example.brewco.data.dto

import androidx.annotation.Keep

@Keep
data class PaymentRequest(
    val amount: Int,
    val orderId: String,
    val orderInfo: String,
    val language: String = "vn"
)
