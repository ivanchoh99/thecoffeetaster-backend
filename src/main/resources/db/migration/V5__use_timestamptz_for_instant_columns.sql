-- V5__use_timestamptz_for_instant_columns.sql
-- Hibernate 6 maps kotlin/java.time.Instant to TIMESTAMP_UTC,
-- which PostgreSQL represents as TIMESTAMPTZ (timestamp with time zone).
-- Align all Instant-backed columns with that mapping.

ALTER TABLE users
    ALTER COLUMN created_at TYPE TIMESTAMPTZ,
    ALTER COLUMN updated_at TYPE TIMESTAMPTZ,
    ALTER COLUMN lock_until TYPE TIMESTAMPTZ;

ALTER TABLE revoked_tokens
    ALTER COLUMN revoked_at TYPE TIMESTAMPTZ;

ALTER TABLE issued_refresh_tokens
    ALTER COLUMN issued_at TYPE TIMESTAMPTZ,
    ALTER COLUMN expires_at TYPE TIMESTAMPTZ;

ALTER TABLE password_reset_tokens
    ALTER COLUMN created_at TYPE TIMESTAMPTZ,
    ALTER COLUMN used_at TYPE TIMESTAMPTZ,
    ALTER COLUMN expires_at TYPE TIMESTAMPTZ;

ALTER TABLE audit_logs
    ALTER COLUMN "timestamp" TYPE TIMESTAMPTZ;
