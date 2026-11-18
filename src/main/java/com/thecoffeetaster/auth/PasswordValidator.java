package com.thecoffeetaster.auth;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Component for validating password complexity requirements.
 *
 * Enforces the following password complexity rules:
 * - Minimum 8 characters in length
 * - At least 1 uppercase letter (A-Z)
 * - At least 1 lowercase letter (a-z)
 * - At least 1 digit (0-9)
 * - At least 1 special character from: @$!%*?&
 *
 * This validator is used during:
 * - User registration (RegisterRequest validation)
 * - Password reset (PasswordResetRequest validation)
 * - Password change (ChangePasswordRequest validation)
 *
 * The component uses the Result pattern — validation failures are communicated
 * via AuthResult with an AuthError.PasswordTooWeak variant containing the list
 * of failed requirements, never as thrown exceptions.
 *
 * Example usage:
 * <pre>
 * AuthResult&lt;Void&gt; result = passwordValidator.validate("SecurePass123@");
 * if (result instanceof AuthResult.Success) {
 *     // Password is valid, proceed with registration
 * } else if (result instanceof AuthResult.Failure failure) {
 *     if (failure.error() instanceof AuthError.PasswordTooWeak weak) {
 *         // Handle password weakness with list of failed requirements
 *         System.out.println(weak.failedRequirements()); // e.g., ["Must contain uppercase"]
 *     }
 * }
 * </pre>
 */
@Component
public class PasswordValidator {

    /** Minimum required password length. */
    private static final int MIN_LENGTH = 8;

    /** Set of allowed special characters. */
    private static final String SPECIAL_CHARS = "@$!%*?&";

    /**
     * Validates a password against all complexity requirements.
     *
     * Performs all complexity checks and accumulates any failed requirements.
     * Returns a Success result only if ALL requirements are met; otherwise
     * returns a Failure containing a PasswordTooWeak error with the list of
     * failed requirements.
     *
     * @param password the password string to validate (may be null)
     * @return AuthResult with Success(null) if valid, or Failure(PasswordTooWeak)
     *         if any requirement is not met. The PasswordTooWeak error contains
     *         a list of human-readable failure messages for display to the user.
     *
     * Example return values:
     * - Success if password is "SecurePass123@"
     * - Failure with ["Must contain uppercase", "Must contain special character"]
     *   if password is "securepass123"
     */
    public AuthResult<Void> validate(String password) {
        List<String> failedRequirements = getFailedRequirements(password);

        if (failedRequirements.isEmpty()) {
            // All requirements met — return success with null value
            return new AuthResult.Success<>(null);
        } else {
            // One or more requirements failed — return failure with detailed error
            String failedRulesMessage = String.join(", ", failedRequirements);
            return new AuthResult.Failure<>(
                    new AuthError.PasswordTooWeak(
                            "Password does not meet complexity requirements: " + failedRulesMessage,
                            failedRequirements));
        }
    }

    /**
     * Returns a list of unmet password complexity requirements.
     *
     * Systematically checks each complexity rule and returns only the names of
     * requirements that the password fails to satisfy. This allows callers to:
     * - Display specific feedback to users about what they need to fix
     * - Programmatically analyze which rules are violated
     * - Build dynamic UI validation messages
     *
     * The returned list is immutable to prevent external modification.
     *
     * @param password the password string to check (may be null or empty)
     * @return an unmodifiable List of requirement failure messages. An empty list
     *         means the password satisfies all requirements. Examples:
     *         - Empty list for "SecurePass123@"
     *         - ["Must contain at least 8 characters"] for "Short1@"
     *         - ["Must contain uppercase", "Must contain special character"]
     *           for "securepass123"
     */
    public List<String> getFailedRequirements(String password) {
        List<String> failed = new ArrayList<>();

        // Null or empty password always fails all checks
        if (password == null || password.isEmpty()) {
            failed.add("Must contain at least 8 characters");
            failed.add("Must contain at least one uppercase letter");
            failed.add("Must contain at least one lowercase letter");
            failed.add("Must contain at least one digit");
            failed.add("Must contain at least one special character (@$!%*?&)");
            return failed;
        }

        // Check minimum length requirement
        // A password shorter than 8 characters cannot be considered secure
        if (password.length() < MIN_LENGTH) {
            failed.add("Must contain at least 8 characters");
        }

        // Check for uppercase letter requirement
        // Ensures password contains at least one character in range A-Z
        if (!password.matches(".*[A-Z].*")) {
            failed.add("Must contain at least one uppercase letter");
        }

        // Check for lowercase letter requirement
        // Ensures password contains at least one character in range a-z
        if (!password.matches(".*[a-z].*")) {
            failed.add("Must contain at least one lowercase letter");
        }

        // Check for digit requirement
        // Ensures password contains at least one numeric character (0-9)
        if (!password.matches(".*\\d.*")) {
            failed.add("Must contain at least one digit");
        }

        // Check for special character requirement
        // Ensures password contains at least one character from the allowed set: @$!%*?&
        // The allowed special characters are chosen for broad keyboard/system compatibility
        if (!containsSpecialChar(password)) {
            failed.add("Must contain at least one special character (@$!%*?&)");
        }

        return failed;
    }

    /**
     * Checks whether a password contains at least one allowed special character.
     *
     * Iterates through the password string and checks if any character is in the
     * allowed special characters set: @$!%*?&
     *
     * This method is extracted for clarity and testability — it encapsulates the
     * special character validation logic in one place, making it easier to:
     * - Modify the allowed character set in the future
     * - Unit test special character logic independently
     * - Understand the validation strategy
     *
     * @param password the password string to check (assumed non-null)
     * @return true if at least one character in password is in SPECIAL_CHARS;
     *         false if no special characters are present
     */
    private boolean containsSpecialChar(String password) {
        for (char c : password.toCharArray()) {
            if (SPECIAL_CHARS.indexOf(c) >= 0) {
                return true;
            }
        }
        return false;
    }
}
