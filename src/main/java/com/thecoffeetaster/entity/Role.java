package com.thecoffeetaster.entity;

/**
 * Role enumeration for role-based access control (RBAC).
 * <p>
 * Defines three authorization levels:
 * <ul>
 *   <li>{@link #ANONYMOUS} - Unauthenticated user with no access to protected resources</li>
 *   <li>{@link #ROLE_USER} - Authenticated user with standard access to personal resources</li>
 *   <li>{@link #ROLE_ADMIN} - System administrator with full access to all resources</li>
 * </ul>
 * </p>
 * <p>
 * This enum is compatible with JPA's {@code @Enumerated} annotation for database persistence.
 * Roles are stored as strings in the database and compared during authorization checks.
 * </p>
 * <p>
 * Default role assigned to new users is {@link #ROLE_USER}.
 * </p>
 *
 * @see com.thecoffeetaster.entity.User
 * @since 1.0
 */
public enum Role {
    /**
     * Anonymous role for unauthenticated users.
     * <p>
     * Users with this role have no access to protected resources and cannot perform
     * authenticated operations such as creating brew logs or posting comments.
     * </p>
     */
    ANONYMOUS,

    /**
     * Regular authenticated user role.
     * <p>
     * Users with this role can create and manage their own content (brew logs, reviews),
     * participate in the community, and access user-level features.
     * This is the default role assigned during user registration.
     * </p>
     */
    ROLE_USER,

    /**
     * Administrator role with elevated privileges.
     * <p>
     * Users with this role have full system access and can perform administrative
     * operations such as managing users, moderating content, and accessing audit logs.
     * </p>
     */
    ROLE_ADMIN
}
