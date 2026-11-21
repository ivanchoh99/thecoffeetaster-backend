package com.thecoffeetaster.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.Instant

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
    var userId: Long? = null,

    @Column(name = "event_type", nullable = false)
    var eventType: String = "",

    @Column(name = "timestamp", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP")
    var timestamp: Instant = Instant.now(),

    @Column(name = "ip_address", nullable = true)
    var ipAddress: String? = null,

    @Column(name = "reason", nullable = true)
    var reason: String? = null,

    @Column(name = "count", nullable = true)
    var count: Int? = null,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "details", nullable = true, columnDefinition = "jsonb")
    var details: String? = null
)
