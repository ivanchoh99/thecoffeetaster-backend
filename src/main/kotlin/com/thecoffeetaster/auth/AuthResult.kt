package com.thecoffeetaster.auth

sealed interface AuthResult<out T> {
    data class Success<T>(val value: T?) : AuthResult<T>
    data class Failure<T>(val error: AuthError) : AuthResult<T>
}
