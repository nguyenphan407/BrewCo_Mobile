package com.example.brewco.data.models

import androidx.annotation.Keep

@Keep
data class CartItem(
    val id: String,
    val productName: String,
    val size: String,
    val quantity: Int,
    val price: Int,
    val toppings: List<String>,
    val orderId: String? = null,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val entryId: String = java.util.UUID.randomUUID().toString()
)
