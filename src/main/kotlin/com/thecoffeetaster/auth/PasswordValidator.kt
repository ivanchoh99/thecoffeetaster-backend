package com.thecoffeetaster.auth

import org.springframework.stereotype.Component

@Component
class PasswordValidator {

    private val MIN_LENGTH = 8
    private val SPECIAL_CHARS = "@$!%*?&"

    fun validate(password: String?): AuthResult<Void> {
        val failedRequirements = getFailedRequirements(password)

        return if (failedRequirements.isEmpty()) {
            AuthResult.Success(null)
        } else {
            val failedRulesMessage = failedRequirements.joinToString(", ")
            AuthResult.Failure(
                AuthError.PasswordTooWeak(
                    "Password does not meet complexity requirements: $failedRulesMessage",
                    failedRequirements
                )
            )
        }
    }

    fun getFailedRequirements(password: String?): List<String> {
        val failed = mutableListOf<String>()

        if (password.isNullOrEmpty()) {
            failed.addAll(listOf(
                "Must contain at least 8 characters",
                "Must contain at least one uppercase letter",
                "Must contain at least one lowercase letter",
                "Must contain at least one digit",
                "Must contain at least one special character (@$!%*?&)"
            ))
            return failed
        }

        if (password.length < MIN_LENGTH) {
            failed.add("Must contain at least 8 characters")
        }

        if (!Regex(".*[A-Z].*").matches(password)) {
            failed.add("Must contain at least one uppercase letter")
        }

        if (!Regex(".*[a-z].*").matches(password)) {
            failed.add("Must contain at least one lowercase letter")
        }

        if (!Regex(".*\\d.*").matches(password)) {
            failed.add("Must contain at least one digit")
        }

        if (!containsSpecialChar(password)) {
            failed.add("Must contain at least one special character (@$!%*?&)")
        }

        return failed.toList()
    }

    private fun containsSpecialChar(password: String): Boolean {
        return password.any { SPECIAL_CHARS.indexOf(it) >= 0 }
    }
}
