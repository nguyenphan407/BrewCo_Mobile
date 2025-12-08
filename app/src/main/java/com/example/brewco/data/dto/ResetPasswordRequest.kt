package com.example.brewco.data.dto

data class ResetPasswordRequest(
    val email: String,
    val password: String,
    val passwordConfirm: String
) 
