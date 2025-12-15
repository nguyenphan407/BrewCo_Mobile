package com.example.brewco.data.dto

import androidx.annotation.Keep

@Keep
data class OrderResponse(
    val error: Boolean,
    val statusCode: Int,
    val data: OrderData,
    val message: String
)

@Keep
data class OrderData(
    val content: List<OrderDetail>
)

@Keep
data class OrderDetail(
    val id: String,
    val orderItemList: List<OrderItem>,
    val paymentMethod: String,
    val itemsPrice: Int,
    val totalPrice: Int,
    val orderStatus: Int
)

@Keep
data class OrderItem(
    val id: String,
    val amount: Int,
    val name: String?,
    val price: Int?,
    val description: String?,
    val imageUrl: String?

)
