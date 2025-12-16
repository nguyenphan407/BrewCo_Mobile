package com.example.brewco.data.models

import androidx.annotation.Keep

@Keep
data class Order(
    val id: Int,
    val customerName: String,
    val phoneNumber: String,
    val items: List<CartItem>,
    val orderDate: Long = System.currentTimeMillis(),
    val totalAmount: Int,
    val status: OrderStatus = OrderStatus.PENDING
)

@Keep
data class OrderItem(
    val productId: String,
    val productName: String,
    val quantity: Int,
    val price: Double,
    val note: String = ""
)

@Keep
enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PREPARING,
    READY,
    COMPLETED,
    CANCELLED
}
