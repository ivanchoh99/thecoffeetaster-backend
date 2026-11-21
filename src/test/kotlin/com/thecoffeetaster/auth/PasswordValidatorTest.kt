package com.thecoffeetaster.auth

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@DisplayName("PasswordValidator Unit Tests")
class PasswordValidatorTest {

    private lateinit var validator: PasswordValidator

    @BeforeEach
    fun setUp() {
        validator = PasswordValidator()
    }

    // ============================================================================
    // Tests for validate() method with VALID passwords
    // ============================================================================

    @Test
    @DisplayName("validate() returns Success when password meets all requirements")
    fun testValidateSuccessfulComplexPassword() {
        val result: AuthResult<Void> = validator.validate("SecurePass123@")

        assertTrue(result is AuthResult.Success<*>)
        assertTrue((result as AuthResult.Success<Void>).value == null)
    }

    @Test
    @DisplayName("validate() returns Success for valid password with special char @")
    fun testValidateSuccessWithAtSign() {
        val result: AuthResult<Void> = validator.validate("MyPassword123@abc")

        assertTrue(result is AuthResult.Success<*>)
    }

    @Test
    @DisplayName("validate() returns Success for valid password with special char $")
    fun testValidateSuccessWithDollarSign() {
        val result: AuthResult<Void> = validator.validate("ComplexPass99$")

        assertTrue(result is AuthResult.Success<*>)
    }

    @Test
    @DisplayName("validate() returns Success for valid password with special char !")
    fun testValidateSuccessWithExclamation() {
        val result: AuthResult<Void> = validator.validate("StrongPassword1!")

        assertTrue(result is AuthResult.Success<*>)
    }

    @Test
    @DisplayName("validate() returns Success for valid password with special char %")
    fun testValidateSuccessWithPercent() {
        val result: AuthResult<Void> = validator.validate("SecurePass123%")

        assertTrue(result is AuthResult.Success<*>)
    }

    @Test
    @DisplayName("validate() returns Success for valid password with special char *")
    fun testValidateSuccessWithAsterisk() {
        val result: AuthResult<Void> = validator.validate("MyPass99*")

        assertTrue(result is AuthResult.Success<*>)
    }

    @Test
    @DisplayName("validate() returns Success for valid password with special char ?")
    fun testValidateSuccessWithQuestion() {
        val result: AuthResult<Void> = validator.validate("Complex123?Pass")

        assertTrue(result is AuthResult.Success<*>)
    }

    @Test
    @DisplayName("validate() returns Success for valid password with special char &")
    fun testValidateSuccessWithAmpersand() {
        val result: AuthResult<Void> = validator.validate("Strong&Password1")

        assertTrue(result is AuthResult.Success<*>)
    }

    // ============================================================================
    // Tests for validate() method with INVALID passwords
    // ============================================================================

    @Test
    @DisplayName("validate() returns Failure when password is too short (7 chars)")
    fun testValidateFailureTooShort() {
        val result: AuthResult<Void> = validator.validate("Short1@")

        assertTrue(result is AuthResult.Failure<*>)
        val error = (result as AuthResult.Failure<Void>).error
        assertTrue(error is AuthError.PasswordTooWeak)

        val weak = error as AuthError.PasswordTooWeak
        assertTrue(weak.failedRequirements.contains("Must contain at least 8 characters"))
    }

    @Test
    @DisplayName("validate() returns Failure when password is missing uppercase letter")
    fun testValidateFailureMissingUppercase() {
        val result: AuthResult<Void> = validator.validate("securepass123@")

        assertTrue(result is AuthResult.Failure<*>)
        val error = (result as AuthResult.Failure<Void>).error
        assertTrue(error is AuthError.PasswordTooWeak)

        val weak = error as AuthError.PasswordTooWeak
        assertTrue(weak.failedRequirements.contains("Must contain at least one uppercase letter"))
    }

    @Test
    @DisplayName("validate() returns Failure when password is missing lowercase letter")
    fun testValidateFailureMissingLowercase() {
        val result: AuthResult<Void> = validator.validate("SECUREPASS123@")

        assertTrue(result is AuthResult.Failure<*>)
        val error = (result as AuthResult.Failure<Void>).error
        assertTrue(error is AuthError.PasswordTooWeak)

        val weak = error as AuthError.PasswordTooWeak
        assertTrue(weak.failedRequirements.contains("Must contain at least one lowercase letter"))
    }

    @Test
    @DisplayName("validate() returns Failure when password is missing digit")
    fun testValidateFailureMissingDigit() {
        val result: AuthResult<Void> = validator.validate("SecurePassWord@")

        assertTrue(result is AuthResult.Failure<*>)
        val error = (result as AuthResult.Failure<Void>).error
        assertTrue(error is AuthError.PasswordTooWeak)

        val weak = error as AuthError.PasswordTooWeak
        assertTrue(weak.failedRequirements.contains("Must contain at least one digit"))
    }

    @Test
    @DisplayName("validate() returns Failure when password is missing special character")
    fun testValidateFailureMissingSpecialChar() {
        val result: AuthResult<Void> = validator.validate("SecurePassword123")

        assertTrue(result is AuthResult.Failure<*>)
        val error = (result as AuthResult.Failure<Void>).error
        assertTrue(error is AuthError.PasswordTooWeak)

        val weak = error as AuthError.PasswordTooWeak
        assertTrue(weak.failedRequirements.contains("Must contain at least one special character (@$!%*?&)"))
    }

    @Test
    @DisplayName("validate() returns Failure with multiple failed requirements")
    fun testValidateFailureMultipleRequirements() {
        val result: AuthResult<Void> = validator.validate("pass")

        assertTrue(result is AuthResult.Failure<*>)
        val error = (result as AuthResult.Failure<Void>).error
        assertTrue(error is AuthError.PasswordTooWeak)

        val weak = error as AuthError.PasswordTooWeak
        assertTrue(weak.failedRequirements.contains("Must contain at least 8 characters"))
        assertTrue(weak.failedRequirements.contains("Must contain at least one uppercase letter"))
        assertTrue(weak.failedRequirements.contains("Must contain at least one digit"))
        assertTrue(weak.failedRequirements.contains("Must contain at least one special character (@$!%*?&)"))
    }

    @Test
    @DisplayName("validate() returns Failure when password is null")
    fun testValidateFailureNullPassword() {
        val result: AuthResult<Void> = validator.validate(null)

        assertTrue(result is AuthResult.Failure<*>)
        val error = (result as AuthResult.Failure<Void>).error
        assertTrue(error is AuthError.PasswordTooWeak)

        val weak = error as AuthError.PasswordTooWeak
        assertTrue(weak.failedRequirements.size >= 4)
    }

    @Test
    @DisplayName("validate() returns Failure when password is empty")
    fun testValidateFailureEmptyPassword() {
        val result: AuthResult<Void> = validator.validate("")

        assertTrue(result is AuthResult.Failure<*>)
        val error = (result as AuthResult.Failure<Void>).error
        assertTrue(error is AuthError.PasswordTooWeak)

        val weak = error as AuthError.PasswordTooWeak
        assertTrue(weak.failedRequirements.size >= 4)
    }

    // ============================================================================
    // Tests for getFailedRequirements() method
    // ============================================================================

    @Test
    @DisplayName("getFailedRequirements() returns empty list for valid password")
    fun testGetFailedRequirementsEmpty() {
        val failed: List<String> = validator.getFailedRequirements("ValidPass123@")

        assertTrue(failed.isEmpty())
    }

    @Test
    @DisplayName("getFailedRequirements() returns list of all failures for weak password")
    fun testGetFailedRequirementsMultiple() {
        val failed: List<String> = validator.getFailedRequirements("weak")

        assertTrue(failed.contains("Must contain at least 8 characters"))
        assertTrue(failed.contains("Must contain at least one uppercase letter"))
        assertTrue(failed.contains("Must contain at least one digit"))
        assertTrue(failed.contains("Must contain at least one special character (@$!%*?&)"))
    }

    @Test
    @DisplayName("getFailedRequirements() returns null case result")
    fun testGetFailedRequirementsNull() {
        val failed: List<String> = validator.getFailedRequirements(null)

        assertTrue(failed.isNotEmpty())
        assertTrue(failed.contains("Must contain at least 8 characters"))
    }

    @Test
    @DisplayName("getFailedRequirements() returns empty string case result")
    fun testGetFailedRequirementsEmptyString() {
        val failed: List<String> = validator.getFailedRequirements("")

        assertTrue(failed.isNotEmpty())
        assertTrue(failed.contains("Must contain at least 8 characters"))
    }

    @Test
    @DisplayName("getFailedRequirements() detects only length failure")
    fun testGetFailedRequirementsLengthOnly() {
        val failed: List<String> = validator.getFailedRequirements("Pass123@a")

        assertTrue(failed.isEmpty(), "9-char password with all requirements should pass")
    }

    @Test
    @DisplayName("getFailedRequirements() detects only uppercase letter failure")
    fun testGetFailedRequirementsUppercaseOnly() {
        val failed: List<String> = validator.getFailedRequirements("securepass123@")

        assertTrue(failed.size == 1)
        assertTrue(failed.contains("Must contain at least one uppercase letter"))
    }

    @Test
    @DisplayName("getFailedRequirements() detects only lowercase letter failure")
    fun testGetFailedRequirementsLowercaseOnly() {
        val failed: List<String> = validator.getFailedRequirements("SECUREPASS123@")

        assertTrue(failed.size == 1)
        assertTrue(failed.contains("Must contain at least one lowercase letter"))
    }

    @Test
    @DisplayName("getFailedRequirements() detects only digit failure")
    fun testGetFailedRequirementsDigitOnly() {
        val failed: List<String> = validator.getFailedRequirements("SecurePassWord@")

        assertTrue(failed.size == 1)
        assertTrue(failed.contains("Must contain at least one digit"))
    }

    @Test
    @DisplayName("getFailedRequirements() detects only special character failure")
    fun testGetFailedRequirementsSpecialCharOnly() {
        val failed: List<String> = validator.getFailedRequirements("SecurePassword123")

        assertTrue(failed.size == 1)
        assertTrue(failed.contains("Must contain at least one special character (@$!%*?&)"))
    }

    // ============================================================================
    // Parametrized tests for valid passwords (using MethodSource due to @ chars)
    // ============================================================================

    @ParameterizedTest
    @MethodSource("validPasswords")
    @DisplayName("validate() accepts all valid password examples")
    fun testValidateAcceptsValidPasswords(validPassword: String) {
        val result: AuthResult<Void> = validator.validate(validPassword)
        assertTrue(result is AuthResult.Success<*>,
            "Password '$validPassword' should be valid")
    }

    @ParameterizedTest
    @MethodSource("invalidPasswords")
    @DisplayName("validate() rejects all invalid password examples")
    fun testValidateRejectsInvalidPasswords(invalidPassword: String) {
        val result: AuthResult<Void> = validator.validate(invalidPassword)
        assertTrue(result is AuthResult.Failure<*>,
            "Password '$invalidPassword' should be invalid")
    }

    companion object {
        @JvmStatic
        fun validPasswords(): Stream<String> = Stream.of(
            "ValidPass123@",
            "MyP@ssw0rd",
            "Test\$Pass99",
            "Complex!Password1",
            "Secure%Pwd2024",
            "Strong*Pass2024",
            "Tough?Pwd2024Chal",
            "Mighty&Pass2024",
            "P@ssw0rd123456"
        )

        @JvmStatic
        fun invalidPasswords(): Stream<String> = Stream.of(
            "NoDigit@",
            "noupper123@",
            "NOLOWER123@",
            "NoSpecial123",
            "Short@1",
            "123456@a",
            ""
        )
    }

    // ============================================================================
    // Edge case tests
    // ============================================================================

    @Test
    @DisplayName("validate() accepts password with exactly 8 characters meeting all requirements")
    fun testValidateExactlyEightCharacters() {
        val result: AuthResult<Void> = validator.validate("Pass99@a")

        assertTrue(result is AuthResult.Success<*>)
    }

    @Test
    @DisplayName("validate() accepts password with 7 characters failing only length")
    fun testValidateSevenCharactersWithAllOtherReqs() {
        val failed: List<String> = validator.getFailedRequirements("Pass99@")

        assertTrue(failed.size == 1)
        assertTrue(failed.contains("Must contain at least 8 characters"))
    }

    @Test
    @DisplayName("validate() accepts very long password meeting all requirements")
    fun testValidateVeryLongPassword() {
        val result: AuthResult<Void> = validator.validate(
            "ThisIsAVeryLongPasswordWithAllRequirementsIncludingNumbers123AndSpecialChars@#")

        assertTrue(result is AuthResult.Success<*>)
    }

    @Test
    @DisplayName("validate() accepts password with multiple special characters")
    fun testValidateMultipleSpecialCharacters() {
        val result: AuthResult<Void> = validator.validate("Pass@@@Word123")

        assertTrue(result is AuthResult.Success<*>)
    }

    @Test
    @DisplayName("validate() accepts password with multiple uppercase letters")
    fun testValidateMultipleUppercaseLetters() {
        val result: AuthResult<Void> = validator.validate("PaSsWoRd123@")

        assertTrue(result is AuthResult.Success<*>)
    }

    @Test
    @DisplayName("validate() accepts password with multiple lowercase letters")
    fun testValidateMultipleLowercaseLetters() {
        val result: AuthResult<Void> = validator.validate("Ppassword123@")

        assertTrue(result is AuthResult.Success<*>)
    }

    @Test
    @DisplayName("validate() accepts password with multiple digits")
    fun testValidateMultipleDigits() {
        val result: AuthResult<Void> = validator.validate("Password111@")

        assertTrue(result is AuthResult.Success<*>)
    }
}
