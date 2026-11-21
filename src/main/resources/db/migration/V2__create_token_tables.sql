-- Tabla para almacenar tokens revocados
-- Mantiene un registro de tokens que han sido explícitamente revocados (logout, cambio de contraseña, etc.)
-- Permite verificar rápidamente si un token ha sido revocado durante la validación
CREATE TABLE revoked_tokens (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(255) NOT NULL,
    reason VARCHAR(100),
    revoked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_revoked_tokens_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Índice para búsquedas rápidas de tokens revocados por usuario
CREATE INDEX idx_revoked_tokens_user_id ON revoked_tokens(user_id);

-- Tabla para almacenar tokens de refresco emitidos
-- Mantiene un registro de todos los refresh tokens válidos
-- Permite rastrear sesiones activas y controlar la concurrencia de sesiones
CREATE TABLE issued_refresh_tokens (
    id UUID PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    issued_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_issued_refresh_tokens_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Índice para búsquedas rápidas de tokens por usuario
CREATE INDEX idx_issued_refresh_tokens_user_id ON issued_refresh_tokens(user_id);

-- Índice para limpiar tokens expirados
CREATE INDEX idx_issued_refresh_tokens_expires_at ON issued_refresh_tokens(expires_at);

-- Tabla para almacenar tokens de restablecimiento de contraseña
-- Mantiene un registro de tokens enviados a usuarios para cambiar su contraseña
-- Cada token es de un solo uso y tiene una fecha de expiración
CREATE TABLE password_reset_tokens (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT NOT NULL,
    token_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    used_at TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_password_reset_tokens_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Índice para búsquedas rápidas de tokens por usuario
CREATE INDEX idx_password_reset_tokens_user_id ON password_reset_tokens(user_id);

-- Índice para limpiar tokens expirados
CREATE INDEX idx_password_reset_tokens_expires_at ON password_reset_tokens(expires_at);
