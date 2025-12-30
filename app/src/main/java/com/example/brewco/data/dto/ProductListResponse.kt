package com.example.brewco.data.dto

import androidx.annotation.Keep

import com.google.gson.annotations.SerializedName

@Keep
data class ProductListResponse(
    @SerializedName("error")
    val error: Boolean = false,
    @SerializedName("statusCode")
    val statusCode: Int = 200,
    @SerializedName("data")
    val data: ProductPageData = ProductPageData(),
    @SerializedName("message")
    val message: String? = null
)

@Keep
data class ProductPageData(
    @SerializedName("page")
    val page: Int = 0,
    @SerializedName("pages")
    val pages: Int = 0,
    @SerializedName("size")
    val size: Int = 0,
    @SerializedName("total")
    val total: Int = 0,
    @SerializedName("items")
    val items: List<ProductResponse> = emptyList()
)
