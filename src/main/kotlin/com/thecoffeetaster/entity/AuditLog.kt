package com.thecoffeetaster.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "audit_logs", indexes = [
    Index(name = "idx_audit_logs_user_id", columnList = "user_id"),
    Index(name = "idx_audit_logs_event_type", columnList = "event_type"),
    Index(name = "idx_audit_logs_timestamp", columnList = "timestamp")
])
open class AuditLog(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "user_id", nullable = true)
    var userId: UUID? = null,

    @Column(name = "event_type", nullable = false)
    var eventType: String = "",

    @Column(name = "timestamp", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    var timestamp: Instant = Instant.now(),

    @Column(name = "ip_address", nullable = true)
    var ipAddress: String? = null,

    @Column(name = "reason", nullable = true)
    var reason: String? = null,

    @Column(name = "count", nullable = true)
    var count: Int? = null,

    @Column(name = "details", nullable = true, columnDefinition = "jsonb")
    var details: String? = null
) {

    override fun toString(): String {
        return "AuditLog(id=$id, userId=$userId, eventType='$eventType', timestamp=$timestamp, ipAddress='$ipAddress', reason='$reason', count=$count, details='$details')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AuditLog) return false
        return id == other.id &&
            userId == other.userId &&
            eventType == other.eventType &&
            timestamp == other.timestamp &&
            ipAddress == other.ipAddress &&
            reason == other.reason &&
            count == other.count &&
            details == other.details
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (userId?.hashCode() ?: 0)
        result = 31 * result + eventType.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + (ipAddress?.hashCode() ?: 0)
        result = 31 * result + (reason?.hashCode() ?: 0)
        result = 31 * result + (count?.hashCode() ?: 0)
        result = 31 * result + (details?.hashCode() ?: 0)
        return result
    }
}
