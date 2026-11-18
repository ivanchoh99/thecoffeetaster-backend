package com.thecoffeetaster.auth;

import java.time.Instant;
import java.util.List;

/**
 * Sealed error type representing all expected authentication failure cases.
 * 
 * Each variant corresponds to a specific auth failure scenario. The sealed interface
 * ensures that AuthController can use exhaustive pattern matching via Java switch expressions,
 * with the compiler enforcing that all error variants are handled.
 * 
 * No auth-specific exceptions are thrown — all failures are communicated via AuthError variants.
 */
public sealed interface AuthError permits
        AuthError.InvalidCredentials,
        AuthError.InvalidEmail,
        AuthError.PasswordTooWeak,
        AuthError.PasswordMismatch,
        AuthError.EmailAlreadyRegistered,
        AuthError.InvalidPassword,
        AuthError.AccountLocked,
        AuthError.TokenExpired,
        AuthError.InvalidToken,
        AuthError.TokenRevoked,
        AuthError.UserNotFound,
        AuthError.PasswordResetTokenExpired,
        AuthError.PasswordResetTokenInvalid,
        AuthError.UnexpectedError {

    /**
     * Invalid email or password combination (intentionally generic to prevent email enumeration).
     */
    record InvalidCredentials() implements AuthError {}

    /**
     * Email format is invalid.
     */
    record InvalidEmail(String message) implements AuthError {}

    /**
     * Password does not meet complexity requirements.
     */
    record PasswordTooWeak(String message, List<String> failedRequirements) implements AuthError {}

    /**
     * Password confirmation does not match the password.
     */
    record PasswordMismatch() implements AuthError {}

    /**
     * Email is already registered to another account.
     */
    record EmailAlreadyRegistered() implements AuthError {}

    /**
     * Password verification failed (e.g., BCrypt mismatch).
     */
    record InvalidPassword() implements AuthError {}

    /**
     * Account is temporarily locked due to failed login attempts.
     * Contains the timestamp when the lockout expires.
     */
    record AccountLocked(Instant unlockTime) implements AuthError {}

    /**
     * JWT token has expired.
     */
    record TokenExpired() implements AuthError {}

    /**
     * JWT token is malformed or has invalid signature.
     */
    record InvalidToken(String detail) implements AuthError {}

    /**
     * Token has been revoked (e.g., after logout or password change).
     */
    record TokenRevoked(String reason) implements AuthError {}

    /**
     * User was not found in the database.
     */
    record UserNotFound() implements AuthError {}

    /**
     * Password reset token has expired.
     */
    record PasswordResetTokenExpired() implements AuthError {}

    /**
     * Password reset token is invalid or not found.
     */
    record PasswordResetTokenInvalid(String detail) implements AuthError {}

    /**
     * An unexpected error occurred during authentication.
     */
    record UnexpectedError(String message) implements AuthError {}
}
