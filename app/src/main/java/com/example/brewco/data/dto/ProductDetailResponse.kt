package com.example.brewco.data.dto

class ProductDetailResponse (
    val error: Boolean,
    val statusCode: Int,
    val data: ProductDetail,
    val message: String
)
