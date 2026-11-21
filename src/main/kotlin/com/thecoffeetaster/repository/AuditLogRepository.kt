package com.thecoffeetaster.repository

import com.thecoffeetaster.entity.AuditLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuditLogRepository : JpaRepository<AuditLog, Long> {

    fun findAllByUserId(userId: Long): List<AuditLog>

    fun findAllByEventType(eventType: String): List<AuditLog>

    fun findAllByUserIdAndEventType(userId: Long, eventType: String): List<AuditLog>
}
