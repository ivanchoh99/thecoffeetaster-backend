package com.thecoffeetaster.auth

import java.time.Instant

sealed interface AuthError {
    object InvalidCredentials : AuthError
    data class InvalidEmail(val message: String) : AuthError
    data class PasswordTooWeak(val message: String, val failedRequirements: List<String>) : AuthError
    object PasswordMismatch : AuthError
    object EmailAlreadyRegistered : AuthError
    object InvalidPassword : AuthError
    data class AccountLocked(val unlockTime: Instant) : AuthError
    object TokenExpired : AuthError
    data class InvalidToken(val detail: String) : AuthError
    data class TokenRevoked(val reason: String) : AuthError
    object UserNotFound : AuthError
    object PasswordResetTokenExpired : AuthError
    data class PasswordResetTokenInvalid(val detail: String) : AuthError
    data class UnexpectedError(val message: String) : AuthError
}
