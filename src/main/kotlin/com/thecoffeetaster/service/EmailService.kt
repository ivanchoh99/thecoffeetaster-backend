package com.thecoffeetaster.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

/**
 * Sends transactional emails. Currently only the password reset link.
 */
@Service
class EmailService(
    private val javaMailSender: JavaMailSender,
    @Value("\${app.frontend.base-url:https://thecoffeetaster.com}") private val frontendBaseUrl: String
) {

    fun sendPasswordResetEmail(toEmail: String, token: String) {
        val message = SimpleMailMessage().apply {
            setTo(toEmail)
            subject = "Reset your TheCoffeeTaster password"
            text = """
                We received a request to reset your password.

                Open this link to choose a new one:
                $frontendBaseUrl/reset-password?token=$token

                If you did not request this, you can safely ignore this email.
            """.trimIndent()
        }
        javaMailSender.send(message)
    }
}
