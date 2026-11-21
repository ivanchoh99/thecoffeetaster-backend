package com.thecoffeetaster.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "users")
open class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true, columnDefinition = "UUID")
    var uuid: UUID = UUID.randomUUID(),

    @Column(nullable = false, unique = true)
    var email: String = "",

    @Column(nullable = false)
    var passwordHash: String = "",

    var displayName: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: Role = Role.ROLE_USER,

    @Column(nullable = false)
    var failedLoginCounter: Int = 0,

    @Column(nullable = false)
    var lockedOut: Boolean = false,

    var lockUntil: Instant? = null,

    @Column(nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),

    @Column(nullable = false)
    var updatedAt: Instant = Instant.now()
) {

    @PreUpdate
    protected fun onUpdate() {
        updatedAt = Instant.now()
    }

    fun isLocked(): Boolean =
        lockedOut && (lockUntil?.let { Instant.now().isBefore(it) } ?: true)

    fun clearLockout() {
        this.lockedOut = false
        this.lockUntil = null
        this.failedLoginCounter = 0
    }
}
