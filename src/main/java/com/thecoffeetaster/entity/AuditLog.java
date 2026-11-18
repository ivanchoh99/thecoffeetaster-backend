package com.thecoffeetaster.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity representing an audit log entry for security events and authentication activities.
 * Used to track critical operations like logins, logouts, password resets, and token revocations.
 * 
 * Supports optional user_id (nullable for failed login attempts with unregistered emails),
 * event metadata (reason, count), and JSONB details for extensibility.
 */
@Entity
@Table(name = "audit_logs", indexes = {
    @Index(name = "idx_audit_logs_user_id", columnList = "user_id"),
    @Index(name = "idx_audit_logs_event_type", columnList = "event_type"),
    @Index(name = "idx_audit_logs_timestamp", columnList = "timestamp")
})
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Nullable user ID. Present for authenticated events (login success, logout, etc.),
     * absent for unauthenticated events (failed login with unknown email).
     */
    @Column(name = "user_id", nullable = true)
    private UUID userId;
    
    /**
     * The event type string: LOGIN_SUCCESS, LOGIN_FAILURE, REGISTRATION, PASSWORD_RESET,
     * LOGOUT, TOKEN_REVOKED, TOKENS_REVOKED, etc.
     */
    @Column(name = "event_type", nullable = false)
    private String eventType;
    
    /**
     * When the event occurred.
     */
    @Column(name = "timestamp", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Instant timestamp;
    
    /**
     * Client IP address, if available (from HttpServletRequest).
     */
    @Column(name = "ip_address", nullable = true)
    private String ipAddress;
    
    /**
     * Optional reason for the event: e.g., "INVALID_PASSWORD", "ACCOUNT_LOCKED",
     * "USER_LOGOUT", "PASSWORD_CHANGE", "USER_INITIATED_REVOKE_ALL", etc.
     * Sensitive data (passwords, tokens) is NEVER stored here.
     */
    @Column(name = "reason", nullable = true)
    private String reason;
    
    /**
     * Optional count field for batch operations: number of tokens revoked, etc.
     */
    @Column(name = "count", nullable = true)
    private Integer count;
    
    /**
     * Optional JSONB details for event-specific metadata.
     * Never contains sensitive information like passwords or tokens.
     */
    @Column(name = "details", nullable = true, columnDefinition = "jsonb")
    private String details;
    
    // Constructors
    
    public AuditLog() {
    }
    
    /**
     * Construct an AuditLog with minimal fields. timestamp defaults to now().
     */
    public AuditLog(String eventType, UUID userId, String ipAddress, String reason, Integer count) {
        this.eventType = eventType;
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.reason = reason;
        this.count = count;
        this.timestamp = Instant.now();
    }
    
    /**
     * Construct an AuditLog with all fields. timestamp defaults to now().
     */
    public AuditLog(String eventType, UUID userId, String ipAddress, String reason, Integer count, String details) {
        this.eventType = eventType;
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.reason = reason;
        this.count = count;
        this.details = details;
        this.timestamp = Instant.now();
    }
    
    /**
     * Construct an AuditLog with explicit timestamp.
     */
    public AuditLog(String eventType, UUID userId, String ipAddress, String reason, Integer count, String details, Instant timestamp) {
        this.eventType = eventType;
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.reason = reason;
        this.count = count;
        this.details = details;
        this.timestamp = timestamp;
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
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public Integer getCount() {
        return count;
    }
    
    public void setCount(Integer count) {
        this.count = count;
    }
    
    public String getDetails() {
        return details;
    }
    
    public void setDetails(String details) {
        this.details = details;
    }
    
    // toString
    @Override
    public String toString() {
        return "AuditLog{" +
                "id=" + id +
                ", userId=" + userId +
                ", eventType='" + eventType + '\'' +
                ", timestamp=" + timestamp +
                ", ipAddress='" + ipAddress + '\'' +
                ", reason='" + reason + '\'' +
                ", count=" + count +
                ", details='" + details + '\'' +
                '}';
    }
    
    // equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuditLog auditLog = (AuditLog) o;
        return Objects.equals(id, auditLog.id) &&
                Objects.equals(userId, auditLog.userId) &&
                Objects.equals(eventType, auditLog.eventType) &&
                Objects.equals(timestamp, auditLog.timestamp) &&
                Objects.equals(ipAddress, auditLog.ipAddress) &&
                Objects.equals(reason, auditLog.reason) &&
                Objects.equals(count, auditLog.count) &&
                Objects.equals(details, auditLog.details);
    }
    
    // hashCode
    @Override
    public int hashCode() {
        return Objects.hash(id, userId, eventType, timestamp, ipAddress, reason, count, details);
    }
}
