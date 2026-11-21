package com.thecoffeetaster.service

import com.thecoffeetaster.auth.AuthError
import com.thecoffeetaster.auth.AuthResult
import com.thecoffeetaster.entity.Role
import com.thecoffeetaster.entity.User
import com.thecoffeetaster.repository.UserRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
@Transactional
class UserAccountService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    private val log = LoggerFactory.getLogger(UserAccountService::class.java)

    private val LOCKOUT_DURATION_SECONDS: Long = 15 * 60
    private val LOCKOUT_THRESHOLD = 5

    fun findByEmail(email: String): AuthResult<User> {
        val user = userRepository.findByEmail(email)
        return if (user.isPresent) {
            AuthResult.Success(user.get())
        } else {
            AuthResult.Failure(AuthError.UserNotFound)
        }
    }

    fun findByUuid(uuid: UUID): AuthResult<User> {
        val user = userRepository.findByUuid(uuid)
        return if (user.isPresent) {
            AuthResult.Success(user.get())
        } else {
            AuthResult.Failure(AuthError.UserNotFound)
        }
    }

    fun createUser(email: String, rawPassword: String): AuthResult<User> {
        if (userRepository.findByEmail(email).isPresent) {
            log.warn("Registration attempt with email already registered: {}", email)
            return AuthResult.Failure(AuthError.EmailAlreadyRegistered)
        }

        val hashedPassword = passwordEncoder.encode(rawPassword)

        val newUser = User(
            null,
            UUID.randomUUID(),
            email,
            hashedPassword,
            null,
            Role.ROLE_USER,
            0,
            false,
            null,
            Instant.now(),
            Instant.now()
        )

        val savedUser = userRepository.save(newUser)
        log.info("User account created successfully: {}", savedUser.uuid)

        return AuthResult.Success(savedUser)
    }

    fun incrementFailedLoginCounter(user: User) {
        user.failedLoginCounter = user.failedLoginCounter + 1
        log.debug("Failed login counter incremented for user {}: {}", user.uuid, user.failedLoginCounter)

        if (user.failedLoginCounter >= LOCKOUT_THRESHOLD) {
            applyLockout(user)
        } else {
            userRepository.save(user)
        }
    }

    fun applyLockout(user: User) {
        val lockUntilTime = Instant.now().plusSeconds(LOCKOUT_DURATION_SECONDS)
        user.lockedOut = true
        user.lockUntil = lockUntilTime

        userRepository.save(user)
        log.info("User locked out: {} until {}", user.uuid, lockUntilTime)
    }

    fun clearLockout(user: User) {
        user.clearLockout()
        userRepository.save(user)
        log.debug("Lockout cleared for user {}", user.uuid)
    }

    fun resetLoginCounter(user: User) {
        user.failedLoginCounter = 0
        userRepository.save(user)
        log.debug("Login counter reset for user {}", user.uuid)
    }

    fun isLockedOut(user: User): Boolean {
        return user.isLocked()
    }

    fun isLockoutExpired(user: User): Boolean {
        if (user.lockUntil == null) {
            return false
        }
        return Instant.now().isAfter(user.lockUntil) || Instant.now().equals(user.lockUntil)
    }

    fun updatePassword(user: User, rawPassword: String) {
        val hashedPassword = passwordEncoder.encode(rawPassword)
        user.passwordHash = hashedPassword
        userRepository.save(user)
        log.info("Password updated for user {}", user.uuid)
    }

    fun updateUserRole(userId: UUID, newRole: Role): AuthResult<Void> {
        val findResult = findByUuid(userId)

        if (findResult is AuthResult.Failure<*>) {
            return AuthResult.Failure(AuthError.UserNotFound)
        }

        val user = (findResult as AuthResult.Success<User>).value!!
        user.role = newRole
        userRepository.save(user)
        log.info("User role updated: {} -> {}", userId, newRole)

        return AuthResult.Success(null)
    }
}
