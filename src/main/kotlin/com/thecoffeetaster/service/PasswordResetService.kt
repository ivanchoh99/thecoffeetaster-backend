package com.thecoffeetaster.service

import com.thecoffeetaster.auth.AuthError
import com.thecoffeetaster.auth.AuthResult
import com.thecoffeetaster.auth.JwtProvider
import com.thecoffeetaster.auth.PasswordValidator
import com.thecoffeetaster.entity.PasswordResetToken
import com.thecoffeetaster.repository.PasswordResetTokenRepository
import com.thecoffeetaster.repository.UserRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant

/**
 * Orchestrates the password recovery flow (request email + reset with token).
 */
@Service
@Transactional
class PasswordResetService(
    private val userRepository: UserRepository,
    private val passwordResetTokenRepository: PasswordResetTokenRepository,
    private val jwtProvider: JwtProvider,
    private val passwordValidator: PasswordValidator,
    private val userAccountService: UserAccountService,
    private val tokenRevocationService: TokenRevocationService,
    private val auditLogService: AuditLogService,
    private val emailService: EmailService
) {

    private val log = LoggerFactory.getLogger(PasswordResetService::class.java)

    /**
     * Always completes silently (no email enumeration).
     */
    fun initiatePasswordReset(email: String) {
        val user = userRepository.findByEmail(email).orElse(null) ?: return
        try {
            val token = jwtProvider.generatePasswordResetToken(user.uuid)
            val expiresAt = jwtProvider.getExpiration(token) ?: Instant.now().plusSeconds(3600)
            val userId = requireNotNull(user.id)

            passwordResetTokenRepository.deleteAllByUserId(userId)
            passwordResetTokenRepository.save(
                PasswordResetToken(
                    userId = userId,
                    tokenHash = jwtProvider.hashToken(token),
                    expiresAt = expiresAt
                )
            )
            emailService.sendPasswordResetEmail(user.email, token)
            auditLogService.log(
                eventType = "PASSWORD_RECOVERY_REQUESTED",
                userId = userId,
                reason = "EMAIL_SENT"
            )
        } catch (e: Exception) {
            log.warn("Password recovery request failed: {}", e.message)
        }
    }

    fun validateResetToken(token: String): AuthResult<PasswordResetToken> {
        return when (val validated = jwtProvider.validateToken(token)) {
            is AuthResult.Failure -> AuthResult.Failure(validated.error)
            is AuthResult.Success -> {
                val claims = validated.value
                    ?: return AuthResult.Failure(AuthError.InvalidToken("Missing claims"))
                if (claims.get("type", String::class.java) != "password_reset") {
                    return AuthResult.Failure(AuthError.InvalidToken("Not a password reset token"))
                }
                val resetToken = passwordResetTokenRepository
                    .findByTokenHashAndUsedAtIsNull(jwtProvider.hashToken(token))
                    .orElse(null)
                    ?: return AuthResult.Failure(
                        AuthError.PasswordResetTokenInvalid("Token not found or already used")
                    )
                if (resetToken.isExpired()) {
                    return AuthResult.Failure(AuthError.PasswordResetTokenExpired)
                }
                AuthResult.Success(resetToken)
            }
        }
    }

    fun completePasswordReset(token: String, newPassword: String): AuthResult<Void> {
        val resetToken = when (val validated = validateResetToken(token)) {
            is AuthResult.Failure -> return AuthResult.Failure(validated.error)
            is AuthResult.Success -> validated.value
                ?: return AuthResult.Failure(AuthError.PasswordResetTokenInvalid("Missing token"))
        }

        when (val passwordCheck = passwordValidator.validate(newPassword)) {
            is AuthResult.Failure -> return AuthResult.Failure(passwordCheck.error)
            is AuthResult.Success -> Unit
        }

        val user = userRepository.findById(resetToken.userId).orElse(null)
            ?: return AuthResult.Failure(AuthError.UserNotFound)

        userAccountService.updatePassword(user, newPassword)

        resetToken.markAsUsed()
        passwordResetTokenRepository.save(resetToken)

        val userId = requireNotNull(user.id)
        val revoked = tokenRevocationService.revokeAllTokensForUser(userId, "PASSWORD_CHANGE")
        auditLogService.log(
            eventType = "TOKENS_REVOKED_PASSWORD_CHANGE",
            userId = userId,
            reason = "PASSWORD_CHANGE",
            count = revoked
        )
        auditLogService.log(eventType = "PASSWORD_RESET", userId = userId)

        return AuthResult.Success(null)
    }
}
