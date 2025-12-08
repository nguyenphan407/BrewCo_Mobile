package com.example.brewco.data.dto

data class CreateOrderRequest(
    val address: String = "",
    val fullName: String,
    val totalPrice: Int,
    val orderItems: List<OrderItemRequest>,
    val paymentType: String = "CASH",
    val phone: String,
    val userId: String
)

data class OrderItemRequest(
    val id: String,
    val amount: Int = 1
) 
