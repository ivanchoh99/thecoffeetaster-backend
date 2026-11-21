package com.thecoffeetaster.controller

import com.thecoffeetaster.auth.AuthError
import com.thecoffeetaster.auth.AuthResult
import com.thecoffeetaster.dto.LoginRequestDTO
import com.thecoffeetaster.dto.PasswordRecoveryRequestDTO
import com.thecoffeetaster.dto.PasswordResetRequestDTO
import com.thecoffeetaster.dto.RegisterRequestDTO
import com.thecoffeetaster.dto.response.ApiResponse
import com.thecoffeetaster.dto.response.CurrentUserResponse
import com.thecoffeetaster.dto.response.ErrorDetail
import com.thecoffeetaster.service.AuthService
import com.thecoffeetaster.service.PasswordResetService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.time.Duration
import java.util.UUID

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
    private val passwordResetService: PasswordResetService,
    @Value("\${app.cookie.secure:true}") private val secureCookie: Boolean,
    @Value("\${app.cookie.max-age-days:7}") private val cookieMaxAgeDays: Long
) {

    @PostMapping("/register")
    fun register(
        @Valid @RequestBody request: RegisterRequestDTO,
        http: HttpServletRequest
    ): ResponseEntity<*> = when (val result = authService.register(request, clientIp(http))) {
        is AuthResult.Success -> {
            val session = result.value!!
            ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, refreshCookie(session.refreshToken).toString())
                .body(ApiResponse.success("User registered successfully", session.response))
        }
        is AuthResult.Failure -> errorResponse(result.error)
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequestDTO,
        http: HttpServletRequest
    ): ResponseEntity<*> = when (val result = authService.login(request, clientIp(http))) {
        is AuthResult.Success -> {
            val session = result.value!!
            ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie(session.refreshToken).toString())
                .body(ApiResponse.success("Login successful", session.response))
        }
        is AuthResult.Failure -> errorResponse(result.error)
    }

    @PostMapping("/refresh")
    fun refresh(
        @CookieValue(name = "refreshToken", required = false) refreshToken: String?
    ): ResponseEntity<*> {
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header(HttpHeaders.SET_COOKIE, clearCookie().toString())
                .body(ApiResponse.error("SESSION_EXPIRED", "Session expired. Please log in again"))
        }

        return when (val result = authService.refresh(refreshToken)) {
            is AuthResult.Success -> {
                val session = result.value!!
                ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshCookie(session.refreshToken).toString())
                    .body(ApiResponse.success("Token refreshed", session.response))
            }
            is AuthResult.Failure -> ResponseEntity.status(errorStatus(result.error, HttpStatus.UNAUTHORIZED))
                .header(HttpHeaders.SET_COOKIE, clearCookie().toString())
                .body(errorBody(result.error))
        }
    }

    @PostMapping("/logout")
    fun logout(
        @CookieValue(name = "refreshToken", required = false) refreshToken: String?,
        http: HttpServletRequest
    ): ResponseEntity<*> {
        if (refreshToken != null) {
            authService.logout(refreshToken, clientIp(http))
        }
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, clearCookie().toString())
            .body(ApiResponse.success<Any>("Logged out successfully", null))
    }

    @PostMapping("/logout-all-devices")
    fun logoutAllDevices(
        authentication: Authentication?,
        http: HttpServletRequest
    ): ResponseEntity<*> {
        val userUuid = currentUserUuid(authentication)
            ?: return unauthorized()
        return when (val result = authService.logoutAllDevices(userUuid, clientIp(http))) {
            is AuthResult.Success -> ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearCookie().toString())
                .body(ApiResponse.success("Logged out from all devices successfully", mapOf("revokedTokens" to result.value)))
            is AuthResult.Failure -> errorResponse(result.error)
        }
    }

    @GetMapping("/me")
    fun me(authentication: Authentication?): ResponseEntity<*> {
        val userUuid = currentUserUuid(authentication)
            ?: return unauthorized()
        return when (val result = authService.getCurrentUser(userUuid)) {
            is AuthResult.Success -> ResponseEntity.ok()
                .body(ApiResponse.success("Current user", CurrentUserResponse(result.value!!)))
            is AuthResult.Failure -> errorResponse(result.error)
        }
    }

    @PostMapping("/password-recovery")
    fun passwordRecovery(@Valid @RequestBody request: PasswordRecoveryRequestDTO): ResponseEntity<*> {
        passwordResetService.initiatePasswordReset(request.email)
        return ResponseEntity.ok().body(
            ApiResponse.success<Any>(
                "If an account with this email exists, a password recovery link has been sent",
                null
            )
        )
    }

    @PostMapping("/password-reset")
    fun passwordReset(@Valid @RequestBody request: PasswordResetRequestDTO): ResponseEntity<*> {
        return when (val result = passwordResetService.completePasswordReset(request.token, request.newPassword)) {
            is AuthResult.Success -> ResponseEntity.ok().body(
                ApiResponse.success<Any>("Password successfully reset. Please log in with your new password", null)
            )
            is AuthResult.Failure -> errorResponse(result.error, HttpStatus.BAD_REQUEST)
        }
    }

    // ---------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------

    private fun currentUserUuid(authentication: Authentication?): UUID? =
        authentication?.principal as? UUID

    private fun unauthorized(): ResponseEntity<ApiResponse<ErrorDetail>> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error("UNAUTHORIZED", "Unauthorized"))

    private fun clientIp(request: HttpServletRequest): String? =
        request.getHeader("X-Forwarded-For")?.split(",")?.firstOrNull()?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: request.remoteAddr

    private fun refreshCookie(token: String): ResponseCookie =
        ResponseCookie.from("refreshToken", token)
            .httpOnly(true)
            .secure(secureCookie)
            .sameSite("Strict")
            .path("/api/v1/auth")
            .maxAge(Duration.ofDays(cookieMaxAgeDays))
            .build()

    private fun clearCookie(): ResponseCookie =
        ResponseCookie.from("refreshToken", "")
            .httpOnly(true)
            .secure(secureCookie)
            .sameSite("Strict")
            .path("/api/v1/auth")
            .maxAge(Duration.ZERO)
            .build()

    private fun errorResponse(
        error: AuthError,
        invalidTokenStatus: HttpStatus = HttpStatus.BAD_REQUEST
    ): ResponseEntity<ApiResponse<ErrorDetail>> =
        ResponseEntity.status(errorStatus(error, invalidTokenStatus))
            .body(errorBody(error))

    private fun errorStatus(error: AuthError, invalidTokenStatus: HttpStatus): HttpStatus = when (error) {
        is AuthError.InvalidCredentials, is AuthError.UserNotFound, is AuthError.InvalidPassword ->
            HttpStatus.UNAUTHORIZED
        is AuthError.AccountLocked -> HttpStatus.TOO_MANY_REQUESTS
        is AuthError.EmailAlreadyRegistered -> HttpStatus.CONFLICT
        is AuthError.TokenExpired, is AuthError.TokenRevoked -> HttpStatus.UNAUTHORIZED
        is AuthError.InvalidToken -> invalidTokenStatus
        is AuthError.PasswordTooWeak, is AuthError.PasswordMismatch, is AuthError.InvalidEmail,
        is AuthError.PasswordResetTokenInvalid, is AuthError.PasswordResetTokenExpired ->
            HttpStatus.BAD_REQUEST
        is AuthError.UnexpectedError -> HttpStatus.INTERNAL_SERVER_ERROR
    }

    private fun errorBody(error: AuthError): ApiResponse<ErrorDetail> =
        when (error) {
            is AuthError.InvalidCredentials ->
                ApiResponse.error("INVALID_CREDENTIALS", "Invalid email or password")
            is AuthError.UserNotFound, is AuthError.InvalidPassword ->
                ApiResponse.error("INVALID_CREDENTIALS", "Invalid email or password")
            is AuthError.AccountLocked ->
                ApiResponse.error(
                    "ACCOUNT_LOCKED",
                    "Account temporarily locked due to too many failed login attempts. Try again in ${error.unlockTime}"
                )
            is AuthError.EmailAlreadyRegistered ->
                ApiResponse.error("EMAIL_ALREADY_REGISTERED", "Email already registered")
            is AuthError.TokenExpired ->
                ApiResponse.error("SESSION_EXPIRED", "Session expired. Please log in again")
            is AuthError.TokenRevoked ->
                ApiResponse.error("SESSION_INVALIDATED", "Session has been invalidated. Please log in again")
            is AuthError.InvalidToken ->
                ApiResponse.error("INVALID_TOKEN", error.detail)
            is AuthError.PasswordTooWeak ->
                ApiResponse.error("PASSWORD_COMPLEXITY", error.message)
            is AuthError.PasswordMismatch ->
                ApiResponse.error("PASSWORDS_DO_NOT_MATCH", "Passwords do not match")
            is AuthError.InvalidEmail ->
                ApiResponse.error("INVALID_EMAIL", error.message)
            is AuthError.PasswordResetTokenInvalid ->
                ApiResponse.error("INVALID_RESET_TOKEN", error.detail)
            is AuthError.PasswordResetTokenExpired ->
                ApiResponse.error("INVALID_RESET_TOKEN", "Password reset link is invalid or has expired")
            is AuthError.UnexpectedError ->
                ApiResponse.error("UNEXPECTED_ERROR", error.message)
        }
}
