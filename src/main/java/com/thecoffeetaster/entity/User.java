package com.thecoffeetaster.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    // Identificadores
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, columnDefinition = "UUID")
    private UUID uuid;
    
    // Autenticación
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String passwordHash;
    
    private String displayName;
    
    // Rol y Autorización
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    // Seguridad (lockout)
    @Column(nullable = false)
    private int failedLoginCounter;
    
    @Column(nullable = false)
    private boolean lockedOut;
    
    private Instant lockUntil;
    
    // Auditoría
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    
    @Column(nullable = false)
    private Instant updatedAt;
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (updatedAt == null) {
            updatedAt = Instant.now();
        }
        if (role == null) {
            role = Role.ROLE_USER;
        }
        if (failedLoginCounter == 0) {
            failedLoginCounter = 0;
        }
        if (!lockedOut) {
            lockedOut = false;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
    
    // Métodos de utilidad
    public boolean isLocked() {
        if (!lockedOut) {
            return false;
        }
        if (lockUntil == null) {
            return true;
        }
        return Instant.now().isBefore(lockUntil);
    }
    
    public void clearLockout() {
        this.lockedOut = false;
        this.lockUntil = null;
        this.failedLoginCounter = 0;
    }
}
