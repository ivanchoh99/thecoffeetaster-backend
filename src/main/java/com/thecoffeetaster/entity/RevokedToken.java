package com.thecoffeetaster.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "revoked_tokens", indexes = {
    @Index(name = "idx_revoked_tokens_user_id", columnList = "user_id")
})
public class RevokedToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    @Column(nullable = false)
    private String tokenHash;
    
    @Column(nullable = true)
    private String reason;
    
    @Column(name = "revoked_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Instant revokedAt;
    
    // Constructors
    public RevokedToken() {
    }
    
    public RevokedToken(UUID userId, String tokenHash, String reason) {
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.reason = reason;
        this.revokedAt = Instant.now();
    }
    
    public RevokedToken(UUID userId, String tokenHash, String reason, Instant revokedAt) {
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.reason = reason;
        this.revokedAt = revokedAt;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public UUID getUserId() {
        return userId;
    }
    
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    
    public String getTokenHash() {
        return tokenHash;
    }
    
    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public Instant getRevokedAt() {
        return revokedAt;
    }
    
    public void setRevokedAt(Instant revokedAt) {
        this.revokedAt = revokedAt;
    }
    
    // toString
    @Override
    public String toString() {
        return "RevokedToken{" +
                "id=" + id +
                ", userId=" + userId +
                ", tokenHash='" + tokenHash + '\'' +
                ", reason='" + reason + '\'' +
                ", revokedAt=" + revokedAt +
                '}';
    }
    
    // equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RevokedToken that = (RevokedToken) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(tokenHash, that.tokenHash) &&
                Objects.equals(reason, that.reason) &&
                Objects.equals(revokedAt, that.revokedAt);
    }
    
    // hashCode
    @Override
    public int hashCode() {
        return Objects.hash(id, userId, tokenHash, reason, revokedAt);
    }
}
