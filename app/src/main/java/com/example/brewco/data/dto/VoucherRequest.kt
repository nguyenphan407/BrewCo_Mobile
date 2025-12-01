package com.example.brewco.data.dto

import java.time.LocalDateTime

data class VoucherRequest(
    val discountPercentage: Double,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime
)
