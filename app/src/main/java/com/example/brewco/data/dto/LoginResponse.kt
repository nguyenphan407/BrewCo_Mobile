package com.example.brewco.data.dto

import androidx.annotation.Keep

@Keep
data class LoginResponse(
    val error: Boolean,
    val statusCode: Int,
    val data: LoginData?,
    val message: String?
)

@Keep
data class LoginData(
    val token: String?,
    val refreshToken: String?,
    val expiresIn: LoginTokenExpiry?
)

@Keep
data class LoginTokenExpiry(
    val token: Long?,
    val refreshToken: Long?
)
