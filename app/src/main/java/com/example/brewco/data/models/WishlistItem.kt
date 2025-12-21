package com.example.brewco.data.models

import androidx.annotation.Keep

@Keep
data class WishlistItem(
    val id: String,
    val name: String,
    val price: String,
    val imageRes: Int
)
