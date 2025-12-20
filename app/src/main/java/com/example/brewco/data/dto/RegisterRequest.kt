package com.example.brewco.data.dto

import androidx.annotation.Keep

@Keep
data class RegisterRequest(
    val email: String,
    val password: String,
    val passwordConfirm: String,
    val fullName: String,

)
