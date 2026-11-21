package com.thecoffeetaster.repository

import com.thecoffeetaster.entity.IssuedRefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.Optional
import java.util.UUID

@Repository
interface IssuedRefreshTokenRepository : JpaRepository<IssuedRefreshToken, UUID> {

    fun findAllByUserId(userId: Long): List<IssuedRefreshToken>

    fun findByTokenHash(tokenHash: String): Optional<IssuedRefreshToken>

    fun deleteByExpiresAtBefore(now: Instant)
}
