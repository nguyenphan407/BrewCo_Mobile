package com.example.brewco.data.dto

data class PaymentResponse(
    val code: String,
    val message: String,
    val paymentUrl: String,
    val amount: Int
)
