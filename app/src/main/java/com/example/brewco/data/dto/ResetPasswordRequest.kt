package com.example.brewco.data.dto

import androidx.annotation.Keep

@Keep
data class ResetPasswordRequest(
    val email: String,
    val password: String,
    val passwordConfirm: String
)
