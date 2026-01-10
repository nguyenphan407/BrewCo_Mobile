package com.example.brewco.data.dto

data class UserProfileResponse(
    val id: String,
    val email: String,
    val fullName: String,
    val company: String?,
    val phoneNumber: String?,
    val roles: List<String>,
    val emailVerifiedAt: String?,
    val blockedAt: String?,
    val createdAt: String,
    val updatedAt: String
)
