package com.thecoffeetaster.auth

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.time.Instant
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtProvider(
    @Value("\${jwt.secret}") jwtSecret: String,
    @Value("\${jwt.access-token-expiry}") private val accessTokenExpiry: Int,
    @Value("\${jwt.refresh-token-expiry}") private val refreshTokenExpiry: Int
) {

    private val signingKey: SecretKey = Keys.hmacShaKeyFor(jwtSecret.toByteArray(StandardCharsets.UTF_8))
    private val passwordResetTokenExpiry: Int = 3600 // 1 hour

    fun generateAccessToken(userId: UUID, email: String, role: String): String {
        val now = Instant.now()
        val expiration = now.plusSeconds(accessTokenExpiry.toLong())

        return Jwts.builder()
            .id(UUID.randomUUID().toString())
            .subject(userId.toString())
            .claim("email", email)
            .claim("role", role)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiration))
            .signWith(signingKey)
            .compact()
    }

    fun generateRefreshToken(userId: UUID): String {
        val now = Instant.now()
        val expiration = now.plusSeconds(refreshTokenExpiry.toLong())

        return Jwts.builder()
            .id(UUID.randomUUID().toString())
            .subject(userId.toString())
            .claim("type", "refresh")
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiration))
            .signWith(signingKey)
            .compact()
    }

    fun generatePasswordResetToken(userId: UUID): String {
        val now = Instant.now()
        val expiration = now.plusSeconds(passwordResetTokenExpiry.toLong())

        return Jwts.builder()
            .id(UUID.randomUUID().toString())
            .subject(userId.toString())
            .claim("type", "password_reset")
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiration))
            .signWith(signingKey)
            .compact()
    }

    fun validateToken(token: String): AuthResult<Claims> {
        return try {
            val claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .payload
            AuthResult.Success(claims)
        } catch (e: ExpiredJwtException) {
            AuthResult.Failure(AuthError.TokenExpired)
        } catch (e: JwtException) {
            AuthResult.Failure(AuthError.InvalidToken(e.message ?: "Invalid token"))
        }
    }

    protected fun getClaims(token: String): Optional<Claims> {
        return try {
            val claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .payload
            Optional.of(claims)
        } catch (e: JwtException) {
            Optional.empty()
        }
    }

    fun extractUserUuid(token: String): UUID {
        val claims = getClaims(token).orElseThrow { IllegalArgumentException("Cannot extract UUID from invalid token") }
        return try {
            UUID.fromString(claims.subject)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Token subject is not a valid UUID", e)
        }
    }

    fun extractRole(token: String): String? {
        val claims = getClaims(token).orElseThrow { IllegalArgumentException("Cannot extract role from invalid token") }
        return claims.get("role", String::class.java)
    }

    fun extractTokenType(token: String): String? {
        val claims = getClaims(token).orElseThrow { IllegalArgumentException("Cannot extract token type from invalid token") }
        return claims.get("type", String::class.java)
    }

    fun getExpiration(token: String): Instant? {
        return getClaims(token).map { it.expiration.toInstant() }.orElse(null)
    }

    fun hashToken(token: String): String {
        return try {
            val md = MessageDigest.getInstance("SHA-256")
            val hash = md.digest(token.toByteArray(StandardCharsets.UTF_8))
            hash.joinToString("") { "%02x".format(it) }
        } catch (e: java.security.NoSuchAlgorithmException) {
            throw RuntimeException("SHA-256 algorithm not available", e)
        }
    }
}
