package com.thecoffeetaster.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

/**
 * Request DTOs for authentication endpoints.
 * All DTOs use Jakarta Bean Validation annotations for input validation.
 */

data class RegisterRequestDTO(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be a valid email address")
    val email: String,

    @field:NotBlank(message = "Password is required")
    val password: String,

    @field:NotBlank(message = "Password confirmation is required")
    val passwordConfirm: String
)

data class LoginRequestDTO(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be a valid email address")
    val email: String,

    @field:NotBlank(message = "Password is required")
    val password: String
)

data class PasswordRecoveryRequestDTO(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be a valid email address")
    val email: String
)

data class PasswordResetRequestDTO(
    @field:NotBlank(message = "Password reset token is required")
    val token: String,

    @field:NotBlank(message = "New password is required")
    val newPassword: String,

    @field:NotBlank(message = "Password confirmation is required")
    val newPasswordConfirm: String
)

data class ChangePasswordRequestDTO(
    @field:NotBlank(message = "Current password is required")
    val currentPassword: String,

    @field:NotBlank(message = "New password is required")
    val newPassword: String,

    @field:NotBlank(message = "Password confirmation is required")
    val newPasswordConfirm: String
)
