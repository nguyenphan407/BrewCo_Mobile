package com.example.brewco.data.dto

import androidx.annotation.Keep

@Keep
data class LoginRequest(
    val email: String,
    val password: String,
    val rememberMe: Boolean = false
)
