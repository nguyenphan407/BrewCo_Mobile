package com.example.brewco.data.dto

import androidx.annotation.Keep

@Keep
data class CreateOrderResponse(
    val error: Boolean,
    val statusCode: Int,
    val data: CreatedOrderData?,
    val message: String
)

@Keep
data class CreatedOrderData(
    val id: String,
    val paymentMethod: String?,
    val totalPrice: Int?,
    val orderStatus: Int?,
    val orderItemList: List<CreatedOrderItem> = emptyList()
)

@Keep
data class CreatedOrderItem(
    val id: String,
    val amount: Int,
    val name: String?,
    val price: Int?,
    val description: String?,
    val imageUrl: String?
)
