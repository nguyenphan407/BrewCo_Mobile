package com.example.brewco.data.dto

import androidx.annotation.Keep

@Keep
data class VerifyOtpRequest(
    val email: String,
    val otp: String
)
