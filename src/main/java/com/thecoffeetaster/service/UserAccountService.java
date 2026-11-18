package com.thecoffeetaster.service;

import com.thecoffeetaster.auth.AuthError;
import com.thecoffeetaster.auth.AuthResult;
import com.thecoffeetaster.entity.Role;
import com.thecoffeetaster.entity.User;
import com.thecoffeetaster.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * Service for managing user accounts, including registration, login attempts,
 * lockout management, and password updates.
 *
 * This service is responsible for:
 * - Creating new user accounts with secure password hashing
 * - Tracking failed login attempts and applying lockout restrictions
 * - Managing account lockout state and expiration
 * - Updating user passwords with BCrypt encryption
 * - Retrieving users by email or UUID
 *
 * All methods use the Result pattern (AuthResult<T>) and do not throw custom
 * exceptions for expected authentication failures. Infrastructure errors may
 * still propagate (e.g., database connectivity issues).
 *
 * Password hashing uses BCrypt with a work factor of at least 10, ensuring
 * that password hashes are resistant to brute-force attacks.
 *
 * Lockout is applied after 5 consecutive failed login attempts within a
 * 15-minute window. The lockout duration is 15 minutes, after which the user
 * can attempt login again.
 *
 * Example usage:
 * <pre>
 * // Create a new user with email and password
 * AuthResult&lt;User&gt; result = userAccountService.createUser("user@example.com", "SecurePass123@");
 * if (result instanceof AuthResult.Success&lt;User&gt; success) {
 *     User newUser = success.value();
 *     System.out.println("User created: " + newUser.getUuid());
 * } else if (result instanceof AuthResult.Failure&lt;User&gt; failure) {
 *     if (failure.error() instanceof AuthError.EmailAlreadyRegistered) {
 *         System.out.println("Email already in use");
 *     }
 * }
 *
 * // Find a user by email
 * AuthResult&lt;User&gt; findResult = userAccountService.findByEmail("user@example.com");
 * if (findResult instanceof AuthResult.Success&lt;User&gt; success) {
 *     User user = success.value();
 * }
 * </pre>
 *
 * @see AuthResult
 * @see AuthError
 * @see PasswordEncoder
 * @see UserRepository
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserAccountService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger log = LoggerFactory.getLogger(UserAccountService.class);

    /**
     * Lockout duration in seconds (15 minutes = 900 seconds).
     */
    private static final long LOCKOUT_DURATION_SECONDS = 15 * 60;

    /**
     * Failed login attempts threshold that triggers lockout (5 attempts).
     */
    private static final int LOCKOUT_THRESHOLD = 5;

    /**
     * Finds a user by their email address.
     *
     * This method is used during login and password recovery flows to locate
     * the user in the database. No exception is thrown if the user is not found;
     * instead, a Failure is returned.
     *
     * @param email the user's email address (non-null)
     * @return AuthResult containing:
     *         - Success(User) if the email is found in the database
     *         - Failure(UserNotFound) if no user with that email exists
     *
     * @see #findByUuid(UUID)
     */
    public AuthResult<User> findByEmail(String email) {
        var user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return new AuthResult.Success<>(user.get());
        } else {
            return new AuthResult.Failure<>(new AuthError.UserNotFound());
        }
    }

    /**
     * Finds a user by their UUID (public identifier).
     *
     * This method is used to retrieve a user's profile and during session
     * validation. No exception is thrown if the user is not found; instead,
     * a Failure is returned.
     *
     * @param uuid the user's public UUID (non-null)
     * @return AuthResult containing:
     *         - Success(User) if the UUID is found in the database
     *         - Failure(UserNotFound) if no user with that UUID exists
     *
     * @see #findByEmail(String)
     */
    public AuthResult<User> findByUuid(UUID uuid) {
        var user = userRepository.findByUuid(uuid);
        if (user.isPresent()) {
            return new AuthResult.Success<>(user.get());
        } else {
            return new AuthResult.Failure<>(new AuthError.UserNotFound());
        }
    }

    /**
     * Creates a new user account with the provided email and plaintext password.
     *
     * This method performs the following steps:
     * 1. Check if the email is already registered (returns EmailAlreadyRegistered if so)
     * 2. Hash the password using BCrypt with a work factor of at least 10
     * 3. Generate a UUID for the new user
     * 4. Create the User entity with default role ROLE_USER
     * 5. Save the user to the database
     * 6. Return Success with the newly created User entity
     *
     * The plaintext password is NOT stored. Only the BCrypt hash is persisted.
     *
     * Callers should validate the password complexity BEFORE calling this method
     * using the PasswordValidator component. This service assumes validation has
     * already been performed.
     *
     * The user's UUID is auto-generated via @PrePersist if not already set.
     * The user's role defaults to ROLE_USER.
     * The user's failed login counter and lockout status are initialized to 0/false.
     *
     * @param email the email address for the new user (should be validated by caller)
     * @param rawPassword the plaintext password (should be complexity-validated by caller)
     * @return AuthResult containing:
     *         - Success(User) with the newly created and persisted User entity
     *         - Failure(EmailAlreadyRegistered) if the email is already registered
     *
     * @throws org.springframework.dao.DataAccessException on database errors
     *
     * @see PasswordValidator
     */
    public AuthResult<User> createUser(String email, String rawPassword) {
        // Check if email is already registered
        if (userRepository.findByEmail(email).isPresent()) {
            log.warn("Registration attempt with email already registered: {}", email);
            return new AuthResult.Failure<>(new AuthError.EmailAlreadyRegistered());
        }

        // Hash the plaintext password using BCrypt
        String hashedPassword = passwordEncoder.encode(rawPassword);

        // Create the new User entity with default values
        // Using constructor to avoid Lombok generation dependencies
        User newUser = new User(
                null,  // id (auto-generated by database)
                UUID.randomUUID(),  // uuid
                email,  // email
                hashedPassword,  // passwordHash
                null,  // displayName
                Role.ROLE_USER,  // role
                0,  // failedLoginCounter
                false,  // lockedOut
                null,  // lockUntil
                Instant.now(),  // createdAt
                Instant.now()  // updatedAt
        );

        // Save to database
        User savedUser = userRepository.save(newUser);
        log.info("User account created successfully: {}", savedUser.getUuid());

        return new AuthResult.Success<>(savedUser);
    }

    /**
     * Increments the failed login counter for the given user.
     *
     * This method is called after each failed login attempt. If the counter
     * reaches the lockout threshold (5 attempts), the user is automatically
     * locked out by calling applyLockout().
     *
     * The user's counter is persisted to the database.
     *
     * @param user the User entity to update (non-null, must be managed by JPA)
     *
     * @see #applyLockout(User)
     * @see #resetLoginCounter(User)
     */
    public void incrementFailedLoginCounter(User user) {
        user.setFailedLoginCounter(user.getFailedLoginCounter() + 1);
        log.debug("Failed login counter incremented for user {}: {}",
                user.getUuid(), user.getFailedLoginCounter());

        if (user.getFailedLoginCounter() >= LOCKOUT_THRESHOLD) {
            applyLockout(user);
        } else {
            userRepository.save(user);
        }
    }

    /**
     * Applies a lockout to the user account.
     *
     * This method sets the user's locked_out field to true and sets the lock_until
     * timestamp to 15 minutes from now. The user will not be able to log in until
     * this timestamp has passed.
     *
     * The user's updated state is persisted to the database.
     *
     * @param user the User entity to lock out (non-null, must be managed by JPA)
     *
     * @see #clearLockout(User)
     * @see #isLockedOut(User)
     * @see #isLockoutExpired(User)
     */
    public void applyLockout(User user) {
        Instant lockUntilTime = Instant.now().plusSeconds(LOCKOUT_DURATION_SECONDS);
        user.setLockedOut(true);
        user.setLockUntil(lockUntilTime);

        userRepository.save(user);
        log.info("User locked out: {} until {}", user.getUuid(), lockUntilTime);
    }

    /**
     * Clears the lockout and resets the failed login counter for the given user.
     *
     * This method is called after a successful login or when the lockout period
     * has expired. It delegates to the User entity's clearLockout() method.
     *
     * The user's updated state is persisted to the database.
     *
     * @param user the User entity to clear lockout for (non-null, must be managed by JPA)
     *
     * @see #applyLockout(User)
     * @see #isLockedOut(User)
     */
    public void clearLockout(User user) {
        user.clearLockout();
        userRepository.save(user);
        log.debug("Lockout cleared for user {}", user.getUuid());
    }

    /**
     * Resets the failed login counter to 0 for the given user.
     *
     * This method is called after a successful login to reset the counter but
     * without clearing other lockout state. Typically called when clearing lockout
     * entirely, but can be used independently.
     *
     * The user's updated state is persisted to the database.
     *
     * @param user the User entity to update (non-null, must be managed by JPA)
     *
     * @see #incrementFailedLoginCounter(User)
     * @see #clearLockout(User)
     */
    public void resetLoginCounter(User user) {
        user.setFailedLoginCounter(0);
        userRepository.save(user);
        log.debug("Login counter reset for user {}", user.getUuid());
    }

    /**
     * Checks if the user account is currently locked out.
     *
     * Returns true if the user's locked_out field is true. Note that this does
     * not check if the lockout period has expired; see isLockoutExpired() for that.
     *
     * This method delegates to the User entity's isLocked() method.
     *
     * @param user the User entity to check (non-null)
     * @return true if the user's locked_out field is true; false otherwise
     *
     * @see #isLockoutExpired(User)
     * @see #applyLockout(User)
     */
    public boolean isLockedOut(User user) {
        return user.isLocked();
    }

    /**
     * Checks if the user's lockout period has expired.
     *
     * Returns true if:
     * - The user's lock_until timestamp is not null, AND
     * - The current time is greater than or equal to lock_until
     *
     * Returns false if the lockout is not set or the lockout period has not yet expired.
     *
     * This method is called during login validation to determine if a locked-out user
     * can now attempt to log in again.
     *
     * @param user the User entity to check (non-null)
     * @return true if the lockout period has expired; false otherwise
     *
     * @see #isLockedOut(User)
     * @see #applyLockout(User)
     * @see #clearLockout(User)
     */
    public boolean isLockoutExpired(User user) {
        if (user.getLockUntil() == null) {
            return false;
        }
        return Instant.now().isAfter(user.getLockUntil()) ||
                Instant.now().equals(user.getLockUntil());
    }

    /**
     * Updates the user's password with a new plaintext password.
     *
     * This method hashes the new password using BCrypt with a work factor of at
     * least 10, updates the passwordHash field, and persists the change to the database.
     *
     * Callers should validate the password complexity BEFORE calling this method
     * using the PasswordValidator component. This service assumes validation has
     * already been performed.
     *
     * The plaintext password is NOT stored. Only the BCrypt hash is persisted.
     *
     * @param user the User entity to update (non-null, must be managed by JPA)
     * @param rawPassword the new plaintext password (should be complexity-validated by caller)
     *
     * @see PasswordValidator
     */
    public void updatePassword(User user, String rawPassword) {
        String hashedPassword = passwordEncoder.encode(rawPassword);
        user.setPasswordHash(hashedPassword);
        userRepository.save(user);
        log.info("Password updated for user {}", user.getUuid());
    }

    /**
     * Updates the role of a user account (admin operation).
     *
     * This method is used to change a user's role from ROLE_USER to ROLE_ADMIN
     * or vice versa. The method does NOT check the caller's permissions; that
     * should be enforced via Spring Security @PreAuthorize annotations at the
     * controller level.
     *
     * Returns Failure(UserNotFound) if the user UUID is not found in the database.
     * Returns Success(null) if the role update succeeds.
     *
     * @param userId the user's UUID (public identifier)
     * @param newRole the new Role to assign (non-null)
     * @return AuthResult containing:
     *         - Success(null) if the role update succeeded
     *         - Failure(UserNotFound) if the user UUID was not found
     *
     * @throws org.springframework.dao.DataAccessException on database errors
     */
    public AuthResult<Void> updateUserRole(UUID userId, Role newRole) {
        AuthResult<User> findResult = findByUuid(userId);

        if (findResult instanceof AuthResult.Failure) {
            return new AuthResult.Failure<>(new AuthError.UserNotFound());
        }

        User user = ((AuthResult.Success<User>) findResult).value();
        user.setRole(newRole);
        userRepository.save(user);
        log.info("User role updated: {} -> {}", userId, newRole);

        return new AuthResult.Success<>(null);
    }
}
