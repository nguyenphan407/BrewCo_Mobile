package com.example.brewco.data.dto

import androidx.annotation.Keep

import com.google.gson.annotations.SerializedName

@Keep
data class CategoryListResponse(
    @SerializedName("data")
    val data: List<CategoryResponse> = emptyList()
)
