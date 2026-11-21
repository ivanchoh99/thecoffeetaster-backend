package com.thecoffeetaster.service

import com.thecoffeetaster.entity.AuditLog
import com.thecoffeetaster.repository.AuditLogRepository
import org.springframework.stereotype.Service

/**
 * Persists security/authentication audit events.
 */
@Service
class AuditLogService(
    private val auditLogRepository: AuditLogRepository
) {

    fun log(
        eventType: String,
        userId: Long? = null,
        ipAddress: String? = null,
        reason: String? = null,
        count: Int? = null,
        details: String? = null
    ) {
        auditLogRepository.save(
            AuditLog(
                userId = userId,
                eventType = eventType,
                ipAddress = ipAddress,
                reason = reason,
                count = count,
                details = details
            )
        )
    }
}
