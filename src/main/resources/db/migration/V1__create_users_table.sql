-- V1__create_users_table.sql
-- Creates the users table with dual identifiers (BIGINT ID for internal use, UUID for public API)
-- This table is the core entity for the authentication system supporting:
--   - User registration and login
--   - Account lockout after failed attempts
--   - Role-based access control (RBAC)
--   - Audit trail via created_at and updated_at timestamps

CREATE TABLE users (
    -- Internal primary key: BIGINT auto-generated identity
    -- Used for all foreign key relationships in the database to maintain relational integrity
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,

    -- Public identifier: UUID, unique and externally exposed
    -- Used in JWT claims (sub), API responses, and client-facing URLs
    -- Prevents internal ID leakage and enables secure public identification
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),

    -- Email address: unique identifier for authentication and communication
    -- Case-insensitive, used in login and password recovery flows
    -- Must be validated against RFC 5322 standard before storage
    email VARCHAR(255) NOT NULL UNIQUE,

    -- BCrypt-hashed password: never stored in plaintext
    -- Hash includes salt and work factor (cost ≥ 10)
    -- Compared securely using BCrypt.matches() during authentication
    password_hash VARCHAR(255) NOT NULL,

    -- Display name: optional human-readable name for the user
    -- Can be updated by the user or shown in UI
    display_name VARCHAR(255),

    -- Role: authorization level determining resource access permissions
    -- Valid values: ROLE_USER (default), ROLE_ADMIN
    -- Stored in JWT token claims for stateless authorization checks
    -- Used with @PreAuthorize Spring Security annotations
    role VARCHAR(50) NOT NULL DEFAULT 'ROLE_USER',

    -- Failed login counter: tracks consecutive failed authentication attempts
    -- Incremented on each failed password check
    -- Triggers lockout when reaches 5 within 15-minute window
    -- Reset to 0 on successful login or after lockout expires
    failed_login_counter INT DEFAULT 0,

    -- Lockout flag: indicates if account is temporarily suspended
    -- Set to true when failed_login_counter reaches 5
    -- Prevents authentication during lockout period (checked via lock_until timestamp)
    -- Cleared when lockout_until expires or user successfully authenticates
    locked_out BOOLEAN DEFAULT false,

    -- Lockout expiration timestamp: when the account lockout expires
    -- Set to (current time + 15 minutes) when lockout is applied
    -- Checked to determine if a locked user can attempt login again
    -- If current time >= lock_until, lockout is automatically cleared
    -- NULL when user is not locked out
    lock_until TIMESTAMP,

    -- Account creation timestamp: immutable, set only on insert
    -- Used for audit trail, account age verification, and user analytics
    -- Set automatically to server's current time when user is created
    -- Never modified after initial creation
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Last modification timestamp: updated on every user record change
    -- Used to track when account was last changed (password, role, etc.)
    -- Automatically updated by application logic on changes
    -- Useful for detecting stale cache and detecting concurrent modifications
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index on email for fast login lookups
-- UNIQUE constraint above makes this implicit, but explicit index aids query planner
CREATE INDEX idx_users_email ON users(email);

-- Index on uuid for fast public-ID lookups
-- Used when resolving UUID from JWT sub claim to user record
-- Critical for /api/v1/auth/me and token validation workflows
CREATE INDEX idx_users_uuid ON users(uuid);
