package com.thecoffeetaster.repository;

import com.thecoffeetaster.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio para la entidad AuditLog.
 * 
 * Extiende JpaRepository para proporcionar operaciones CRUD automáticas.
 * Spring Data JPA genera la implementación en tiempo de ejecución.
 * 
 * Proporciona métodos especializados para consultas de auditoría por usuario
 * y tipo de evento, facilitando el análisis de seguridad y rastreo de acciones.
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    /**
     * Encuentra todos los registros de auditoría asociados a un usuario específico.
     * 
     * Utilizado para auditoría por usuario, permitiendo revisar todas las acciones
     * realizadas por un usuario particular. Esencial para análisis de seguridad,
     * investigación de incidentes y cumplimiento normativo.
     *
     * @param userId el UUID único del usuario cuyos registros se desean recuperar
     * @return una lista de todos los registros de auditoría del usuario;
     *         retorna una lista vacía si el usuario no tiene registros
     */
    List<AuditLog> findAllByUserId(UUID userId);
    
    /**
     * Encuentra todos los registros de auditoría de un tipo de evento específico.
     * 
     * Utilizado para rastrear eventos de seguridad críticos (ej: LOGIN_FAILURE,
     * TOKENS_REVOKED, PASSWORD_RESET) a nivel de sistema. Permite identificar
     * patrones de ataque o anomalías en el comportamiento.
     *
     * @param eventType el tipo de evento a buscar (ej: "LOGIN_SUCCESS", "LOGIN_FAILURE",
     *                  "REGISTRATION", "PASSWORD_RESET", "TOKENS_REVOKED")
     * @return una lista de todos los registros de auditoría del tipo especificado;
     *         retorna una lista vacía si no hay registros de ese tipo
     */
    List<AuditLog> findAllByEventType(String eventType);
    
    /**
     * Encuentra todos los registros de auditoría para un usuario y tipo de evento específicos.
     * 
     * Combina filtros de usuario y evento para análisis granular. Permite investigar
     * acciones particulares de un usuario (ej: todos los intentos de login fallidos de un usuario),
     * facilitando la detección de patrones de ataque dirigidos y la auditoría de operaciones
     * sensibles como cambios de contraseña o revocación de tokens.
     *
     * @param userId el UUID único del usuario
     * @param eventType el tipo de evento a buscar
     * @return una lista de registros de auditoría que coinciden con ambos criterios;
     *         retorna una lista vacía si no hay registros que coincidan
     */
    List<AuditLog> findAllByUserIdAndEventType(UUID userId, String eventType);
}
