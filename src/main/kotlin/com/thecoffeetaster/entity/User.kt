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

    @PrePersist
    protected fun onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID()
        }
        if (createdAt == null) {
            createdAt = Instant.now()
        }
        if (updatedAt == null) {
            updatedAt = Instant.now()
        }
        if (role == Role.ROLE_USER && id == null) {
            // role defaults handled by parameter default
        }
        if (failedLoginCounter == 0) {
            // already 0 by default
        }
        if (!lockedOut) {
            // already false by default
        }
    }

    @PreUpdate
    protected fun onUpdate() {
        updatedAt = Instant.now()
    }

    fun isLocked(): Boolean {
        if (!lockedOut) {
            return false
        }
        if (lockUntil == null) {
            return true
        }
        return Instant.now().isBefore(lockUntil)
    }

    fun clearLockout() {
        this.lockedOut = false
        this.lockUntil = null
        this.failedLoginCounter = 0
    }
}
