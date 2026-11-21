package com.thecoffeetaster.repository

import com.thecoffeetaster.entity.AuditLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.List
import java.util.UUID

@Repository
interface AuditLogRepository : JpaRepository<AuditLog, Long> {

    fun findAllByUserId(userId: UUID): List<AuditLog>

    fun findAllByEventType(eventType: String): List<AuditLog>

    fun findAllByUserIdAndEventType(userId: UUID, eventType: String): List<AuditLog>
}
