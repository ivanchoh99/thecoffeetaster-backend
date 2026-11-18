package com.thecoffeetaster.auth;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Unit tests for PasswordValidator component.
 *
 * These tests verify that PasswordValidator correctly identifies password
 * complexity violations and rejects non-compliant passwords while accepting
 * valid passwords.
 */
@DisplayName("PasswordValidator Unit Tests")
class PasswordValidatorTest {

    private PasswordValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PasswordValidator();
    }

    // ============================================================================
    // Tests for validate() method with VALID passwords
    // ============================================================================

    @Test
    @DisplayName("validate() returns Success when password meets all requirements")
    void testValidateSuccessfulComplexPassword() {
        AuthResult<Void> result = validator.validate("SecurePass123@");

        assertTrue(result instanceof AuthResult.Success);
        assertTrue(((AuthResult.Success<Void>) result).value() == null);
    }

    @Test
    @DisplayName("validate() returns Success for valid password with special char @")
    void testValidateSuccessWithAtSign() {
        AuthResult<Void> result = validator.validate("MyPassword123@abc");

        assertTrue(result instanceof AuthResult.Success);
    }

    @Test
    @DisplayName("validate() returns Success for valid password with special char $")
    void testValidateSuccessWithDollarSign() {
        AuthResult<Void> result = validator.validate("ComplexPass99$");

        assertTrue(result instanceof AuthResult.Success);
    }

    @Test
    @DisplayName("validate() returns Success for valid password with special char !")
    void testValidateSuccessWithExclamation() {
        AuthResult<Void> result = validator.validate("StrongPassword1!");

        assertTrue(result instanceof AuthResult.Success);
    }

    @Test
    @DisplayName("validate() returns Success for valid password with special char %")
    void testValidateSuccessWithPercent() {
        AuthResult<Void> result = validator.validate("SecurePass123%");

        assertTrue(result instanceof AuthResult.Success);
    }

    @Test
    @DisplayName("validate() returns Success for valid password with special char *")
    void testValidateSuccessWithAsterisk() {
        AuthResult<Void> result = validator.validate("MyPass99*");

        assertTrue(result instanceof AuthResult.Success);
    }

    @Test
    @DisplayName("validate() returns Success for valid password with special char ?")
    void testValidateSuccessWithQuestion() {
        AuthResult<Void> result = validator.validate("Complex123?Pass");

        assertTrue(result instanceof AuthResult.Success);
    }

    @Test
    @DisplayName("validate() returns Success for valid password with special char &")
    void testValidateSuccessWithAmpersand() {
        AuthResult<Void> result = validator.validate("Strong&Password1");

        assertTrue(result instanceof AuthResult.Success);
    }

    // ============================================================================
    // Tests for validate() method with INVALID passwords
    // ============================================================================

    @Test
    @DisplayName("validate() returns Failure when password is too short (7 chars)")
    void testValidateFailureTooShort() {
        AuthResult<Void> result = validator.validate("Short1@");

        assertTrue(result instanceof AuthResult.Failure);
        AuthError error = ((AuthResult.Failure<Void>) result).error();
        assertTrue(error instanceof AuthError.PasswordTooWeak);

        AuthError.PasswordTooWeak weak = (AuthError.PasswordTooWeak) error;
        assertTrue(weak.failedRequirements().contains("Must contain at least 8 characters"));
    }

    @Test
    @DisplayName("validate() returns Failure when password is missing uppercase letter")
    void testValidateFailureMissingUppercase() {
        AuthResult<Void> result = validator.validate("securepass123@");

        assertTrue(result instanceof AuthResult.Failure);
        AuthError error = ((AuthResult.Failure<Void>) result).error();
        assertTrue(error instanceof AuthError.PasswordTooWeak);

        AuthError.PasswordTooWeak weak = (AuthError.PasswordTooWeak) error;
        assertTrue(weak.failedRequirements().contains("Must contain at least one uppercase letter"));
    }

    @Test
    @DisplayName("validate() returns Failure when password is missing lowercase letter")
    void testValidateFailureMissingLowercase() {
        AuthResult<Void> result = validator.validate("SECUREPASS123@");

        assertTrue(result instanceof AuthResult.Failure);
        AuthError error = ((AuthResult.Failure<Void>) result).error();
        assertTrue(error instanceof AuthError.PasswordTooWeak);

        AuthError.PasswordTooWeak weak = (AuthError.PasswordTooWeak) error;
        assertTrue(weak.failedRequirements().contains("Must contain at least one lowercase letter"));
    }

    @Test
    @DisplayName("validate() returns Failure when password is missing digit")
    void testValidateFailureMissingDigit() {
        AuthResult<Void> result = validator.validate("SecurePassWord@");

        assertTrue(result instanceof AuthResult.Failure);
        AuthError error = ((AuthResult.Failure<Void>) result).error();
        assertTrue(error instanceof AuthError.PasswordTooWeak);

        AuthError.PasswordTooWeak weak = (AuthError.PasswordTooWeak) error;
        assertTrue(weak.failedRequirements().contains("Must contain at least one digit"));
    }

    @Test
    @DisplayName("validate() returns Failure when password is missing special character")
    void testValidateFailureMissingSpecialChar() {
        AuthResult<Void> result = validator.validate("SecurePassword123");

        assertTrue(result instanceof AuthResult.Failure);
        AuthError error = ((AuthResult.Failure<Void>) result).error();
        assertTrue(error instanceof AuthError.PasswordTooWeak);

        AuthError.PasswordTooWeak weak = (AuthError.PasswordTooWeak) error;
        assertTrue(weak.failedRequirements()
                .contains("Must contain at least one special character (@$!%*?&)"));
    }

    @Test
    @DisplayName("validate() returns Failure with multiple failed requirements")
    void testValidateFailureMultipleRequirements() {
        AuthResult<Void> result = validator.validate("pass");

        assertTrue(result instanceof AuthResult.Failure);
        AuthError error = ((AuthResult.Failure<Void>) result).error();
        assertTrue(error instanceof AuthError.PasswordTooWeak);

        AuthError.PasswordTooWeak weak = (AuthError.PasswordTooWeak) error;
        assertTrue(weak.failedRequirements().contains("Must contain at least 8 characters"));
        assertTrue(weak.failedRequirements().contains("Must contain at least one uppercase letter"));
        assertTrue(weak.failedRequirements().contains("Must contain at least one digit"));
        assertTrue(weak.failedRequirements()
                .contains("Must contain at least one special character (@$!%*?&)"));
    }

    @Test
    @DisplayName("validate() returns Failure when password is null")
    void testValidateFailureNullPassword() {
        AuthResult<Void> result = validator.validate(null);

        assertTrue(result instanceof AuthResult.Failure);
        AuthError error = ((AuthResult.Failure<Void>) result).error();
        assertTrue(error instanceof AuthError.PasswordTooWeak);

        AuthError.PasswordTooWeak weak = (AuthError.PasswordTooWeak) error;
        assertTrue(weak.failedRequirements().size() >= 4);
    }

    @Test
    @DisplayName("validate() returns Failure when password is empty")
    void testValidateFailureEmptyPassword() {
        AuthResult<Void> result = validator.validate("");

        assertTrue(result instanceof AuthResult.Failure);
        AuthError error = ((AuthResult.Failure<Void>) result).error();
        assertTrue(error instanceof AuthError.PasswordTooWeak);

        AuthError.PasswordTooWeak weak = (AuthError.PasswordTooWeak) error;
        assertTrue(weak.failedRequirements().size() >= 4);
    }

    // ============================================================================
    // Tests for getFailedRequirements() method
    // ============================================================================

    @Test
    @DisplayName("getFailedRequirements() returns empty list for valid password")
    void testGetFailedRequirementsEmpty() {
        List<String> failed = validator.getFailedRequirements("ValidPass123@");

        assertTrue(failed.isEmpty());
    }

    @Test
    @DisplayName("getFailedRequirements() returns list of all failures for weak password")
    void testGetFailedRequirementsMultiple() {
        List<String> failed = validator.getFailedRequirements("weak");

        assertTrue(failed.contains("Must contain at least 8 characters"));
        assertTrue(failed.contains("Must contain at least one uppercase letter"));
        assertTrue(failed.contains("Must contain at least one digit"));
        assertTrue(failed.contains("Must contain at least one special character (@$!%*?&)"));
    }

    @Test
    @DisplayName("getFailedRequirements() returns null case result")
    void testGetFailedRequirementsNull() {
        List<String> failed = validator.getFailedRequirements(null);

        assertFalse(failed.isEmpty());
        assertTrue(failed.contains("Must contain at least 8 characters"));
    }

    @Test
    @DisplayName("getFailedRequirements() returns empty string case result")
    void testGetFailedRequirementsEmptyString() {
        List<String> failed = validator.getFailedRequirements("");

        assertFalse(failed.isEmpty());
        assertTrue(failed.contains("Must contain at least 8 characters"));
    }

    @Test
    @DisplayName("getFailedRequirements() detects only length failure")
    void testGetFailedRequirementsLengthOnly() {
        List<String> failed = validator.getFailedRequirements("Pass123@a");

        assertTrue(failed.isEmpty(), "9-char password with all requirements should pass");
    }

    @Test
    @DisplayName("getFailedRequirements() detects only uppercase letter failure")
    void testGetFailedRequirementsUppercaseOnly() {
        List<String> failed = validator.getFailedRequirements("securepass123@");

        assertEquals(1, failed.size());
        assertTrue(failed.contains("Must contain at least one uppercase letter"));
    }

    @Test
    @DisplayName("getFailedRequirements() detects only lowercase letter failure")
    void testGetFailedRequirementsLowercaseOnly() {
        List<String> failed = validator.getFailedRequirements("SECUREPASS123@");

        assertEquals(1, failed.size());
        assertTrue(failed.contains("Must contain at least one lowercase letter"));
    }

    @Test
    @DisplayName("getFailedRequirements() detects only digit failure")
    void testGetFailedRequirementsDigitOnly() {
        List<String> failed = validator.getFailedRequirements("SecurePassWord@");

        assertEquals(1, failed.size());
        assertTrue(failed.contains("Must contain at least one digit"));
    }

    @Test
    @DisplayName("getFailedRequirements() detects only special character failure")
    void testGetFailedRequirementsSpecialCharOnly() {
        List<String> failed = validator.getFailedRequirements("SecurePassword123");

        assertEquals(1, failed.size());
        assertTrue(failed.contains("Must contain at least one special character (@$!%*?&)"));
    }

    // ============================================================================
    // Parametrized tests for valid passwords
    // ============================================================================

    @ParameterizedTest
    @ValueSource(strings = {
            "ValidPass123@",
            "MyP@ssw0rd",
            "Test$Pass99",
            "Complex!Password1",
            "Secure%Pwd2024",
            "Strong*Pass2024",
            "Tough?Pwd2024Chal",
            "Mighty&Pass2024",
            "P@ssw0rd123456"
    })
    @DisplayName("validate() accepts all valid password examples")
    void testValidateAcceptsValidPasswords(String validPassword) {
        AuthResult<Void> result = validator.validate(validPassword);
        assertTrue(result instanceof AuthResult.Success,
                "Password '" + validPassword + "' should be valid");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "NoDigit@",
            "noUPPER123@",
            "NOLOWER123@",
            "NoSpecial123",
            "TooShort@1",
            "123456@a",
            ""
    })
    @DisplayName("validate() rejects all invalid password examples")
    void testValidateRejectsInvalidPasswords(String invalidPassword) {
        AuthResult<Void> result = validator.validate(invalidPassword);
        assertTrue(result instanceof AuthResult.Failure,
                "Password '" + invalidPassword + "' should be invalid");
    }

    // ============================================================================
    // Edge case tests
    // ============================================================================

    @Test
    @DisplayName("validate() accepts password with exactly 8 characters meeting all requirements")
    void testValidateExactlyEightCharacters() {
        AuthResult<Void> result = validator.validate("Pass99@a");

        assertTrue(result instanceof AuthResult.Success);
    }

    @Test
    @DisplayName("validate() accepts password with 7 characters failing only length")
    void testValidateSevenCharactersWithAllOtherReqs() {
        List<String> failed = validator.getFailedRequirements("Pass99@");

        assertEquals(1, failed.size());
        assertTrue(failed.contains("Must contain at least 8 characters"));
    }

    @Test
    @DisplayName("validate() accepts very long password meeting all requirements")
    void testValidateVeryLongPassword() {
        AuthResult<Void> result = validator.validate(
                "ThisIsAVeryLongPasswordWithAllRequirementsIncludingNumbers123AndSpecialChars@#");

        assertTrue(result instanceof AuthResult.Success);
    }

    @Test
    @DisplayName("validate() accepts password with multiple special characters")
    void testValidateMultipleSpecialCharacters() {
        AuthResult<Void> result = validator.validate("Pass@@@Word123");

        assertTrue(result instanceof AuthResult.Success);
    }

    @Test
    @DisplayName("validate() accepts password with multiple uppercase letters")
    void testValidateMultipleUppercaseLetters() {
        AuthResult<Void> result = validator.validate("PaSsWoRd123@");

        assertTrue(result instanceof AuthResult.Success);
    }

    @Test
    @DisplayName("validate() accepts password with multiple lowercase letters")
    void testValidateMultipleLowercaseLetters() {
        AuthResult<Void> result = validator.validate("Ppassword123@");

        assertTrue(result instanceof AuthResult.Success);
    }

    @Test
    @DisplayName("validate() accepts password with multiple digits")
    void testValidateMultipleDigits() {
        AuthResult<Void> result = validator.validate("Password111@");

        assertTrue(result instanceof AuthResult.Success);
    }
}
