package com.example.brewco.data.dto

import androidx.annotation.Keep

import com.google.gson.annotations.SerializedName

@Keep
data class ProductListResponse(
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("statusCode")
    val statusCode: Int,
    @SerializedName("data")
    val data: ProductPageData,
    @SerializedName("message")
    val message: String
)

@Keep
data class ProductPageData(
    @SerializedName("page")
    val page: Int,
    @SerializedName("pages")
    val pages: Int,
    @SerializedName("size")
    val size: Int,
    @SerializedName("total")
    val total: Int,
    @SerializedName("items")
    val items: List<ProductResponse>
)
