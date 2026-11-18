package com.thecoffeetaster.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request DTOs for authentication endpoints.
 * All DTOs use Jakarta Bean Validation annotations for input validation.
 */

/**
 * DTO for user registration requests.
 * 
 * @param email The user's email address. Must be a valid email format and not already registered.
 * @param password The user's password. Must meet complexity requirements:
 *                 - Minimum 8 characters
 *                 - At least 1 uppercase letter
 *                 - At least 1 lowercase letter
 *                 - At least 1 digit
 *                 - At least 1 special character from @$!%*?&
 * @param passwordConfirm The password confirmation. Must match the password field exactly.
 */
public record RegisterRequestDTO(
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    String email,

    @NotBlank(message = "Password is required")
    String password,

    @NotBlank(message = "Password confirmation is required")
    String passwordConfirm
) {}

/**
 * DTO for user login requests.
 * 
 * @param email The user's email address. Must be a valid email format.
 * @param password The user's password.
 */
public record LoginRequestDTO(
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    String email,

    @NotBlank(message = "Password is required")
    String password
) {}

/**
 * DTO for password recovery requests.
 * 
 * @param email The user's email address. Must be a valid email format.
 *              If an account exists with this email, a password recovery link will be sent.
 */
public record PasswordRecoveryRequestDTO(
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    String email
) {}

/**
 * DTO for password reset requests.
 * 
 * @param token The password reset token received via email. Must not be empty.
 *              The token must be valid, not expired, and not previously used.
 * @param newPassword The new password. Must meet complexity requirements:
 *                    - Minimum 8 characters
 *                    - At least 1 uppercase letter
 *                    - At least 1 lowercase letter
 *                    - At least 1 digit
 *                    - At least 1 special character from @$!%*?&
 * @param newPasswordConfirm The new password confirmation. Must match the newPassword field exactly.
 */
public record PasswordResetRequestDTO(
    @NotBlank(message = "Password reset token is required")
    String token,

    @NotBlank(message = "New password is required")
    String newPassword,

    @NotBlank(message = "Password confirmation is required")
    String newPasswordConfirm
) {}

/**
 * DTO for change password requests (when user is authenticated).
 * 
 * @param currentPassword The user's current password for verification.
 * @param newPassword The new password. Must meet complexity requirements:
 *                    - Minimum 8 characters
 *                    - At least 1 uppercase letter
 *                    - At least 1 lowercase letter
 *                    - At least 1 digit
 *                    - At least 1 special character from @$!%*?&
 * @param newPasswordConfirm The new password confirmation. Must match the newPassword field exactly.
 */
public record ChangePasswordRequestDTO(
    @NotBlank(message = "Current password is required")
    String currentPassword,

    @NotBlank(message = "New password is required")
    String newPassword,

    @NotBlank(message = "Password confirmation is required")
    String newPasswordConfirm
) {}
