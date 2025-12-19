package com.example.brewco.data.dto

import androidx.annotation.Keep

@Keep
class ProductDetailResponse (
    val error: Boolean,
    val statusCode: Int,
    val data: ProductDetail,
    val message: String
)
