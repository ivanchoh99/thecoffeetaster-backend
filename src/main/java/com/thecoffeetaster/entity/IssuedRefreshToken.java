package com.thecoffeetaster.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * IssuedRefreshToken entity tracks all issued refresh tokens for a user.
 *
 * Purpose: This entity enables bulk revocation (logout from all devices), session management,
 * and security auditing. Only the token hash is stored (SHA-256) — the raw token is never persisted.
 *
 * Use cases:
 * - Identify all tokens issued to a user for revocation during password change
 * - Revoke all sessions during account compromise or security incident
 * - Track session count per user
 * - Clean up expired tokens
 *
 * Relationships:
 * - userId: References a User's public UUID (not a foreign key, as UUID is not the User's primary key)
 * - tokenHash: UNIQUE constraint ensures each token can only be issued once
 *
 * Indexes:
 * - idx_user_id: For queries like "find all tokens for user X"
 * - idx_expires_at: For cleanup queries like "delete all tokens expired before date Y"
 */
@Entity
@Table(
    name = "issued_refresh_tokens",
    indexes = {
        @Index(name = "idx_issued_refresh_tokens_user_id", columnList = "user_id"),
        @Index(name = "idx_issued_refresh_tokens_expires_at", columnList = "expires_at")
    }
)
public class IssuedRefreshToken {

    /**
     * Unique identifier for this issued token record (UUID primary key).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Public UUID of the user who owns this token.
     * This is the user's external identifier, not a foreign key reference.
     */
    @Column(nullable = false)
    private UUID userId;

    /**
     * SHA-256 hash of the refresh token.
     * Unique constraint ensures each token (and its hash) can only be issued once.
     * Used for checking revocation status without storing the raw token.
     */
    @Column(nullable = false, unique = true)
    private String tokenHash;

    /**
     * Timestamp when this token was issued.
     * Used for audit tracking and token lifecycle management.
     */
    @Column(nullable = false)
    private Instant issuedAt;

    /**
     * Timestamp when this token expires.
     * Typically 7 days from issuedAt (sliding window model).
     * Used for cleanup queries and expiration validation.
     */
    @Column(nullable = false)
    private Instant expiresAt;

    /**
     * Default constructor for JPA.
     */
    public IssuedRefreshToken() {
    }

    /**
     * Constructor for creating an IssuedRefreshToken.
     *
     * @param userId    the public UUID of the user who owns this token
     * @param tokenHash the SHA-256 hash of the refresh token (never the raw token)
     * @param issuedAt  the timestamp when the token was issued
     * @param expiresAt the timestamp when the token expires
     */
    public IssuedRefreshToken(UUID userId, String tokenHash, Instant issuedAt, Instant expiresAt) {
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }

    // ========== Utility Methods ==========

    /**
     * Check if this token has expired.
     * A token is considered expired if the current time is after its expiresAt timestamp.
     *
     * @return true if the token has expired, false otherwise
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    // ========== Getters ==========
    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    // ========== Setters ==========
    public void setId(UUID id) {
        this.id = id;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public void setIssuedAt(Instant issuedAt) {
        this.issuedAt = issuedAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    // ========== Standard Object Methods ==========

    @Override
    public String toString() {
        return "IssuedRefreshToken{" +
                "id=" + id +
                ", userId=" + userId +
                ", tokenHash='" + (tokenHash != null ? tokenHash.substring(0, Math.min(16, tokenHash.length())) + "..." : "null") + '\'' +
                ", issuedAt=" + issuedAt +
                ", expiresAt=" + expiresAt +
                ", isExpired=" + isExpired() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IssuedRefreshToken that = (IssuedRefreshToken) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(tokenHash, that.tokenHash) &&
                Objects.equals(issuedAt, that.issuedAt) &&
                Objects.equals(expiresAt, that.expiresAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, tokenHash, issuedAt, expiresAt);
    }
}
