package com.example.criteriolocal.domain.management

import com.example.criteriolocal.domain.model.User

data class RegisterUserRequest(
    val name: String,
    val email: String,
    val password: String,
    val registeredOn: String,
)

data class LoginRequest(
    val email: String,
    val password: String,
)

data class BasicUserProfile(
    val user: User,
    val totalRatings: Int,
)
