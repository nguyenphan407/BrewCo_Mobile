package com.example.brewco.data.dto

data class CreateOrderResponse(
    val error: Boolean,
    val statusCode: Int,
    val data: CreatedOrderData?,
    val message: String
)

data class CreatedOrderData(
    val id: String,
    val paymentMethod: String?,
    val totalPrice: Int?,
    val orderStatus: Int?,
    val orderItemList: List<CreatedOrderItem> = emptyList()
)

data class CreatedOrderItem(
    val id: String,
    val amount: Int,
    val name: String?,
    val price: Int?,
    val description: String?,
    val imageUrl: String?
) 
