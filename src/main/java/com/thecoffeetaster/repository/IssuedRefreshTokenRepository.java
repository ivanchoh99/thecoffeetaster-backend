package com.thecoffeetaster.repository;

import com.thecoffeetaster.entity.IssuedRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for managing IssuedRefreshToken entities.
 *
 * Purpose: Provides persistence operations for tracking issued refresh tokens,
 * enabling session management, bulk revocation (logout from all devices),
 * and cleanup of expired tokens.
 *
 * All queries use the token hash (SHA-256) as the primary lookup key to avoid
 * storing or exposing raw tokens. This repository complements TokenRevocationService
 * in implementing the sliding-window refresh token model.
 */
@Repository
public interface IssuedRefreshTokenRepository extends JpaRepository<IssuedRefreshToken, UUID> {

    /**
     * Find all issued refresh tokens for a specific user.
     *
     * Purpose: Support bulk operations such as:
     * - Revoke all tokens during password change
     * - Revoke all sessions on account compromise
     * - Count active sessions per user
     * - Query all tokens to identify which ones to mark as revoked
     *
     * The returned list includes both valid (not-yet-expired) and expired tokens.
     * Callers must filter by expiresAt timestamp if only active tokens are needed.
     *
     * Performance: Backed by index on user_id column for efficient retrieval,
     * even with large numbers of tokens per user.
     *
     * @param userId the public UUID of the user
     * @return a list of all issued refresh tokens for the user (may be empty)
     */
    List<IssuedRefreshToken> findAllByUserId(UUID userId);

    /**
     * Find an issued refresh token by its SHA-256 hash.
     *
     * Purpose: Look up a token record after parsing and validating the JWT signature,
     * to confirm the token was legitimately issued and retrieve metadata such as:
     * - issuedAt timestamp (for audit)
     * - expiresAt timestamp (validation before refresh)
     * - userId (for cross-validation with JWT claims)
     *
     * Used during token refresh flow to verify the token exists and hasn't been
     * revoked (revoked tokens remain in this table but are marked in revoked_tokens).
     *
     * Performance: Backed by UNIQUE constraint on token_hash column for O(1) lookup.
     *
     * @param tokenHash the SHA-256 hex-encoded hash of the refresh token
     * @return an Optional containing the token record if found and not revoked,
     *         or empty if the token hash does not exist
     */
    Optional<IssuedRefreshToken> findByTokenHash(String tokenHash);

    /**
     * Delete all issued refresh tokens that have expired before or at the given instant.
     *
     * Purpose: Clean up stale token records from the database to prevent unbounded growth
     * of the issued_refresh_tokens table. Expired tokens can no longer be used and are
     * safe to remove after expiration.
     *
     * Cleanup strategy: Call this method periodically (e.g., daily via scheduled task)
     * with Instant.now() to remove all tokens that passed their expiration time.
     * The revoked_tokens table (in TokenRevocationService) retains explicit revocations
     * for audit purposes and may be cleaned separately if needed.
     *
     * Performance: Uses index on expires_at column for efficient range deletion.
     * Typically not called in the critical path; can be scheduled off-peak.
     *
     * @param now the cutoff instant; all tokens with expiresAt <= now are deleted
     *            (typically Instant.now())
     */
    void deleteByExpiresAtBefore(Instant now);
}
