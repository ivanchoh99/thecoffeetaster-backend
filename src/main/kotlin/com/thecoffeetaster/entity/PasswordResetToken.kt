package com.thecoffeetaster.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "password_reset_tokens",
    indexes = [
        Index(name = "idx_user_id", columnList = "user_id"),
        Index(name = "idx_expires_at", columnList = "expires_at")
    ]
)
open class PasswordResetToken(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "user_id", nullable = false, columnDefinition = "UUID")
    var userId: UUID,

    @Column(nullable = false)
    var tokenHash: String,

    @Column(nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),

    var usedAt: Instant? = null,

    @Column(nullable = false)
    var expiresAt: Instant
) {

    @PrePersist
    protected fun onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now()
        }
    }

    fun isExpired(): Boolean {
        return Instant.now().isAfter(expiresAt)
    }

    fun isUsed(): Boolean {
        return usedAt != null
    }

    fun markAsUsed() {
        this.usedAt = Instant.now()
    }
}
