package com.thecoffeetaster.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "password_reset_tokens", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_expires_at", columnList = "expires_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {
    
    // Identificador
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Referencia al usuario (por UUID público)
    @Column(name = "user_id", nullable = false, columnDefinition = "UUID")
    private UUID userId;
    
    // Hash del token
    @Column(nullable = false)
    private String tokenHash;
    
    // Auditoría temporal
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    
    // Timestamp de uso (null = no usado)
    private Instant usedAt;
    
    // Timestamp de expiración
    @Column(nullable = false)
    private Instant expiresAt;
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
    
    // Métodos de utilidad
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
    
    public boolean isUsed() {
        return usedAt != null;
    }
    
    public void markAsUsed() {
        this.usedAt = Instant.now();
    }
}
