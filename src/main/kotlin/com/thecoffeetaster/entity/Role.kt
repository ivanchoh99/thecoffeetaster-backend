package com.thecoffeetaster.entity

/**
 * Role enumeration for role-based access control (RBAC).
 *
 * Defines three authorization levels:
 * - [ANONYMOUS] - Unauthenticated user with no access to protected resources
 * - [ROLE_USER] - Authenticated user with standard access to personal resources
 * - [ROLE_ADMIN] - System administrator with full access to all resources
 *
 * This enum is compatible with JPA's {@code @Enumerated} annotation for database persistence.
 * Roles are stored as strings in the database and compared during authorization checks.
 *
 * Default role assigned to new users is [ROLE_USER].
 */
enum class Role {
    /**
     * Anonymous role for unauthenticated users.
     */
    ANONYMOUS,

    /**
     * Regular authenticated user role.
     * This is the default role assigned during user registration.
     */
    ROLE_USER,

    /**
     * Administrator role with elevated privileges.
     */
    ROLE_ADMIN
}
