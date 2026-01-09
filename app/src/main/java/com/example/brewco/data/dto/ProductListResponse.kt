package com.example.brewco.data.dto
package com.example.brewco.data.dto

import com.google.gson.annotations.SerializedName

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
import com.google.gson.annotations.SerializedName

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
