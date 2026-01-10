package com.example.brewco.data.dto

data class LoginRequest(
    val email: String,
    val password: String,
    val rememberMe: Boolean = false
) 
