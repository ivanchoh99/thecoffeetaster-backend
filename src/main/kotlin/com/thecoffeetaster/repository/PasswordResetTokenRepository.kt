package com.thecoffeetaster.repository

import com.thecoffeetaster.entity.PasswordResetToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface PasswordResetTokenRepository : JpaRepository<PasswordResetToken, Long> {

    fun findByTokenHashAndUsedAtIsNull(tokenHash: String): Optional<PasswordResetToken>

    fun deleteAllByUserId(userId: Long)
}
