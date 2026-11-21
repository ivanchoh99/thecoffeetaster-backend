package com.thecoffeetaster.service

import com.thecoffeetaster.auth.JwtProvider
import com.thecoffeetaster.entity.IssuedRefreshToken
import com.thecoffeetaster.entity.RevokedToken
import com.thecoffeetaster.repository.IssuedRefreshTokenRepository
import com.thecoffeetaster.repository.RevokedTokenRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant

/**
 * Manages the `revoked_tokens` and `issued_refresh_tokens` tables.
 */
@Service
@Transactional
class TokenRevocationService(
    private val revokedTokenRepository: RevokedTokenRepository,
    private val issuedRefreshTokenRepository: IssuedRefreshTokenRepository,
    private val jwtProvider: JwtProvider
) {

    private val log = LoggerFactory.getLogger(TokenRevocationService::class.java)

    fun revokeToken(token: String, userId: Long, reason: String) {
        val hash = jwtProvider.hashToken(token)
        if (!revokedTokenRepository.existsByTokenHash(hash)) {
            revokedTokenRepository.save(
                RevokedToken(userId = userId, tokenHash = hash, reason = reason)
            )
        }
        issuedRefreshTokenRepository.findByTokenHash(hash)
            .ifPresent { issuedRefreshTokenRepository.delete(it) }
        log.debug("Token revoked for user {} (reason={})", userId, reason)
    }

    fun revokeAllTokensForUser(userId: Long, reason: String): Int {
        val issued = issuedRefreshTokenRepository.findAllByUserId(userId)
        var count = 0
        issued.forEach { token ->
            if (!revokedTokenRepository.existsByTokenHash(token.tokenHash)) {
                revokedTokenRepository.save(
                    RevokedToken(userId = userId, tokenHash = token.tokenHash, reason = reason)
                )
                count++
            }
            issuedRefreshTokenRepository.delete(token)
        }
        log.info("Revoked {} tokens for user {} (reason={})", count, userId, reason)
        return count
    }

    fun isRevoked(tokenHash: String): Boolean = revokedTokenRepository.existsByTokenHash(tokenHash)

    fun recordIssuedToken(token: String, userId: Long, expiresAt: Instant) {
        val hash = jwtProvider.hashToken(token)
        issuedRefreshTokenRepository.save(
            IssuedRefreshToken(
                userId = userId,
                tokenHash = hash,
                issuedAt = Instant.now(),
                expiresAt = expiresAt
            )
        )
    }
}
