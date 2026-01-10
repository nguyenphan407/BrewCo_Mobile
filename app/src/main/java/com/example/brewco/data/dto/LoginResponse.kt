package com.example.brewco.data.dto

data class LoginResponse(
    val error: Boolean,
    val statusCode: Int,
    val data: LoginData?,
    val message: String?
)

data class LoginData(
    val token: String?,
    val refreshToken: String?,
    val expiresIn: LoginTokenExpiry?
)

data class LoginTokenExpiry(
    val token: Long?,
    val refreshToken: Long?
)
