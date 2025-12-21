package com.example.brewco.data.models

import androidx.annotation.Keep

@Keep
data class Voucher(
    val id: String,
    val title: String,
    val type: String,
    val discount: String,
    val expiry: String
)
