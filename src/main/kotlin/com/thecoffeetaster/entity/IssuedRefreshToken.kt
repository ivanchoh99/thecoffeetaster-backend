package com.thecoffeetaster.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "issued_refresh_tokens",
    indexes = [
        Index(name = "idx_issued_refresh_tokens_user_id", columnList = "user_id"),
        Index(name = "idx_issued_refresh_tokens_expires_at", columnList = "expires_at")
    ]
)
open class IssuedRefreshToken(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(nullable = false)
    var userId: Long,

    @Column(nullable = false, unique = true)
    var tokenHash: String,

    @Column(nullable = false)
    var issuedAt: Instant,

    @Column(nullable = false)
    var expiresAt: Instant
) {

    fun isExpired(): Boolean {
        return Instant.now().isAfter(expiresAt)
    }
}
