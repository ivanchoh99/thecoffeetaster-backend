package com.thecoffeetaster.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "revoked_tokens", indexes = [
    Index(name = "idx_revoked_tokens_user_id", columnList = "user_id")
])
open class RevokedToken(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "user_id", nullable = false)
    var userId: Long,

    @Column(nullable = false)
    var tokenHash: String,

    var reason: String? = null,

    @Column(name = "revoked_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP")
    var revokedAt: Instant = Instant.now()
)
