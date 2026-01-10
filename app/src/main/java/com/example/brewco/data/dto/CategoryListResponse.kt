package com.example.brewco.data.dto

import com.google.gson.annotations.SerializedName

data class CategoryListResponse(
    @SerializedName("data")
    val data: List<CategoryResponse>
)
