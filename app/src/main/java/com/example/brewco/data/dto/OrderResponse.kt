package com.example.brewco.data.dto

data class OrderResponse(
    val error: Boolean,
    val statusCode: Int,
    val data: OrderData,
    val message: String
)

data class OrderData(
    val content: List<OrderDetail> // Chỉ lấy content, bỏ qua các trường phân trang
)

data class OrderDetail(
    val id: String,
    val orderItemList: List<OrderItem>,
    val paymentMethod: String,
    val itemsPrice: Int,
    val totalPrice: Int,
    val orderStatus: Int
)

data class OrderItem(
    val id: String,
    val amount: Int,
    val name: String?,
    val price: Int?,
    val description: String?,
    val imageUrl: String?
    // Bỏ các trường lồng như productId, productCreatedAt nếu không cần
)