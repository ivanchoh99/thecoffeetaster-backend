-- Tabla para almacenar registros de auditoría
-- Mantiene un registro de eventos de seguridad y autenticación para auditoría y análisis forense
-- Permite rastrear intentos de acceso, cambios de contraseña, revocación de tokens, etc.
CREATE TABLE audit_logs (
    -- Identificador único del registro de auditoría
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    
    -- Identificador del usuario asociado al evento
    -- Nullable para eventos que ocurren sin autenticación (ej: intentos de login fallidos sin usuario identificado)
    user_id BIGINT,
    
    -- Tipo de evento de auditoría
    -- Ejemplos: LOGIN_SUCCESS, LOGIN_FAILURE, REGISTRATION, LOGOUT, TOKEN_REVOKED, 
    -- TOKENS_REVOKED_PASSWORD_CHANGE, PASSWORD_RESET, ACCOUNT_LOCKED, LOCKOUT_EXPIRED
    event_type VARCHAR(50) NOT NULL,
    
    -- Timestamp del evento
    -- Se registra automáticamente con la zona horaria del servidor
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Dirección IP desde la cual se originó el evento
    -- Útil para detectar accesos sospechosos o patrones de ataque
    ip_address VARCHAR(45),
    
    -- Razón o motivo del evento
    -- Ejemplos: Contraseña incorrecta, Token revocado por cambio de contraseña, etc.
    reason VARCHAR(100),
    
    -- Contador de eventos (opcional)
    -- Usado en eventos que afectan múltiples recursos (ej: TOKENS_REVOKED - cantidad de tokens revocados)
    count INTEGER,
    
    -- Datos adicionales del evento en formato JSON
    -- Contiene información detallada del evento como UserAgent, detalles de validación, etc.
    -- Tipo JSONB en PostgreSQL para búsquedas y indexación eficiente
    details JSONB,
    
    -- Restricción de clave externa a la tabla users
    -- Se elimina en cascada si el usuario es eliminado
    CONSTRAINT fk_audit_logs_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Índice para búsquedas rápidas de eventos por usuario
-- Usado para obtener el historial de eventos de un usuario específico
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);

-- Índice para búsquedas por tipo de evento
-- Permite análisis rápido de tipos específicos de eventos (ej: todos los LOGIN_FAILURE)
CREATE INDEX idx_audit_logs_event_type ON audit_logs(event_type);

-- Índice para búsquedas por timestamp
-- Esencial para análisis de eventos en rangos de tiempo específicos
-- Útil para detectar ataques o analizar patrones de acceso
CREATE INDEX idx_audit_logs_timestamp ON audit_logs(timestamp);

