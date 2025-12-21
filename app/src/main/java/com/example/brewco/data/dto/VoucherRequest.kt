package com.example.brewco.data.dto

import androidx.annotation.Keep

import java.time.LocalDateTime

@Keep
data class VoucherRequest(
    val discountPercentage: Double,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime
)
