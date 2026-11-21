package com.thecoffeetaster.repository

import com.thecoffeetaster.entity.RevokedToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.List
import java.util.UUID

@Repository
interface RevokedTokenRepository : JpaRepository<RevokedToken, Long> {

    fun existsByTokenHash(tokenHash: String): Boolean

    fun findAllByUserId(userId: UUID): List<RevokedToken>
}
