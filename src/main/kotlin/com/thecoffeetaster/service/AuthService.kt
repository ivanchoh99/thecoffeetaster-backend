package com.thecoffeetaster.service

import com.thecoffeetaster.auth.AuthError
import com.thecoffeetaster.auth.AuthResult
import com.thecoffeetaster.auth.JwtProvider
import com.thecoffeetaster.auth.PasswordValidator
import com.thecoffeetaster.dto.LoginRequestDTO
import com.thecoffeetaster.dto.RegisterRequestDTO
import com.thecoffeetaster.dto.response.AuthSession
import com.thecoffeetaster.dto.response.LoginResponse
import com.thecoffeetaster.dto.response.RefreshResponse
import com.thecoffeetaster.dto.response.RefreshSession
import com.thecoffeetaster.dto.response.UserProfile
import com.thecoffeetaster.entity.User
import jakarta.transaction.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

/**
 * Main authentication orchestrator. All expected failures are returned as
 * `AuthResult.Failure(AuthError.*)` — never thrown.
 */
@Service
@Transactional
class AuthService(
    private val jwtProvider: JwtProvider,
    private val userAccountService: UserAccountService,
    private val tokenRevocationService: TokenRevocationService,
    private val auditLogService: AuditLogService,
    private val passwordValidator: PasswordValidator,
    private val passwordEncoder: PasswordEncoder
) {

    fun register(request: RegisterRequestDTO, ipAddress: String?): AuthResult<AuthSession> {
        if (request.password != request.passwordConfirm) {
            return AuthResult.Failure(AuthError.PasswordMismatch)
        }

        when (val check = passwordValidator.validate(request.password)) {
            is AuthResult.Failure -> return AuthResult.Failure(check.error)
            is AuthResult.Success -> Unit
        }

        val user = when (val created = userAccountService.createUser(request.email, request.password)) {
            is AuthResult.Failure -> return AuthResult.Failure(created.error)
            is AuthResult.Success -> created.value
                ?: return AuthResult.Failure(AuthError.UnexpectedError("User creation returned no value"))
        }

        val tokens = issueTokens(user)
        auditLogService.log(eventType = "REGISTRATION", userId = user.id, ipAddress = ipAddress)

        return AuthResult.Success(
            AuthSession(LoginResponse(user.toProfile(), tokens.accessToken), tokens.refreshToken)
        )
    }

    fun login(request: LoginRequestDTO, ipAddress: String?): AuthResult<AuthSession> {
        val user = when (val found = userAccountService.findByEmail(request.email)) {
            is AuthResult.Failure -> return AuthResult.Failure(AuthError.InvalidCredentials)
            is AuthResult.Success -> found.value
                ?: return AuthResult.Failure(AuthError.InvalidCredentials)
        }

        if (user.isLocked()) {
            if (!userAccountService.isLockoutExpired(user)) {
                return AuthResult.Failure(AuthError.AccountLocked(user.lockUntil ?: Instant.now()))
            }
            userAccountService.clearLockout(user)
        }

        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            userAccountService.incrementFailedLoginCounter(user)
            auditLogService.log(
                eventType = "LOGIN_FAILURE",
                userId = user.id,
                ipAddress = ipAddress,
                reason = if (user.isLocked()) "ACCOUNT_LOCKED" else "INVALID_PASSWORD"
            )
            return if (user.isLocked()) {
                AuthResult.Failure(AuthError.AccountLocked(user.lockUntil ?: Instant.now()))
            } else {
                AuthResult.Failure(AuthError.InvalidCredentials)
            }
        }

        userAccountService.resetLoginCounter(user)
        val tokens = issueTokens(user)
        auditLogService.log(eventType = "LOGIN_SUCCESS", userId = user.id, ipAddress = ipAddress)

        return AuthResult.Success(
            AuthSession(LoginResponse(user.toProfile(), tokens.accessToken), tokens.refreshToken)
        )
    }

    fun refresh(refreshToken: String): AuthResult<RefreshSession> {
        val claims = when (val validated = jwtProvider.validateToken(refreshToken)) {
            is AuthResult.Failure -> return AuthResult.Failure(validated.error)
            is AuthResult.Success -> validated.value
                ?: return AuthResult.Failure(AuthError.InvalidToken("Missing claims"))
        }

        if (claims.get("type", String::class.java) != "refresh") {
            return AuthResult.Failure(AuthError.InvalidToken("Not a refresh token"))
        }

        val tokenHash = jwtProvider.hashToken(refreshToken)
        if (tokenRevocationService.isRevoked(tokenHash)) {
            return AuthResult.Failure(AuthError.TokenRevoked("REVOKED"))
        }

        val userUuid = runCatching { UUID.fromString(claims.subject) }.getOrNull()
            ?: return AuthResult.Failure(AuthError.InvalidToken("Invalid subject"))

        val user = when (val found = userAccountService.findByUuid(userUuid)) {
            is AuthResult.Failure -> return AuthResult.Failure(AuthError.UserNotFound)
            is AuthResult.Success -> found.value
                ?: return AuthResult.Failure(AuthError.UserNotFound)
        }

        // Rotate: the previous refresh token can no longer be reused.
        tokenRevocationService.revokeToken(
            token = refreshToken,
            userId = requireNotNull(user.id),
            reason = "TOKEN_ROTATED"
        )

        val tokens = issueTokens(user)
        return AuthResult.Success(
            RefreshSession(RefreshResponse(tokens.accessToken), tokens.refreshToken)
        )
    }

    fun logout(refreshToken: String, ipAddress: String?): AuthResult<Void> {
        // Best effort: always report success, even if the token was already invalid.
        val claims = (jwtProvider.validateToken(refreshToken) as? AuthResult.Success)?.value
        if (claims != null) {
            val userUuid = runCatching { UUID.fromString(claims.subject) }.getOrNull()
            val user = userUuid?.let { uuid ->
                (userAccountService.findByUuid(uuid) as? AuthResult.Success)?.value
            }
            if (user?.id != null) {
                tokenRevocationService.revokeToken(refreshToken, user.id!!, "USER_LOGOUT")
                auditLogService.log(
                    eventType = "LOGOUT",
                    userId = user.id,
                    ipAddress = ipAddress
                )
                auditLogService.log(
                    eventType = "TOKEN_REVOKED",
                    userId = user.id,
                    ipAddress = ipAddress,
                    reason = "USER_LOGOUT"
                )
            }
        }
        return AuthResult.Success(null)
    }

    fun logoutAllDevices(userUuid: UUID, ipAddress: String?): AuthResult<Int> {
        val user = when (val found = userAccountService.findByUuid(userUuid)) {
            is AuthResult.Failure -> return AuthResult.Failure(AuthError.UserNotFound)
            is AuthResult.Success -> found.value
                ?: return AuthResult.Failure(AuthError.UserNotFound)
        }

        val userId = requireNotNull(user.id)
        val count = tokenRevocationService.revokeAllTokensForUser(userId, "USER_INITIATED_REVOKE_ALL")
        auditLogService.log(
            eventType = "TOKENS_REVOKED",
            userId = userId,
            ipAddress = ipAddress,
            reason = "USER_INITIATED_REVOKE_ALL",
            count = count
        )
        return AuthResult.Success(count)
    }

    fun getCurrentUser(userUuid: UUID): AuthResult<UserProfile> {
        return when (val found = userAccountService.findByUuid(userUuid)) {
            is AuthResult.Failure -> AuthResult.Failure(AuthError.UserNotFound)
            is AuthResult.Success -> {
                val user = found.value ?: return AuthResult.Failure(AuthError.UserNotFound)
                AuthResult.Success(user.toProfile())
            }
        }
    }

    private data class TokenPair(val accessToken: String, val refreshToken: String)

    private fun issueTokens(user: User): TokenPair {
        val userId = requireNotNull(user.id)
        val accessToken = jwtProvider.generateAccessToken(user.uuid, user.email, user.role.name)
        val refreshToken = jwtProvider.generateRefreshToken(user.uuid)
        val expiresAt = jwtProvider.getExpiration(refreshToken) ?: Instant.now().plusSeconds(604800)
        tokenRevocationService.recordIssuedToken(refreshToken, userId, expiresAt)
        return TokenPair(accessToken, refreshToken)
    }

    private fun User.toProfile(): UserProfile = UserProfile(
        id = uuid,
        email = email,
        displayName = displayName ?: "",
        role = role,
        createdAt = createdAt
    )
}
