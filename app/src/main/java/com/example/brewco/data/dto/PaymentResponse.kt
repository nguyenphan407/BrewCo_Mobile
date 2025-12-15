package com.example.brewco.data.dto

import androidx.annotation.Keep

@Keep
data class PaymentResponse(
    val code: String,
    val message: String,
    val paymentUrl: String,
    val amount: Int
)
