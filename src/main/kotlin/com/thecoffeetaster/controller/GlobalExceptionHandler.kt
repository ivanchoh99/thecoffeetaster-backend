package com.thecoffeetaster.controller

import com.thecoffeetaster.dto.response.ApiResponse
import com.thecoffeetaster.dto.response.ErrorDetail
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException

/**
 * Slim catch-all advice for infrastructure/framework exceptions only.
 * Auth-specific errors are handled in AuthController via AuthResult matching.
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: AccessDeniedException): ResponseEntity<ApiResponse<ErrorDetail>> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            ApiResponse.error("ACCESS_DENIED", "You do not have permission to access this resource")
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<ErrorDetail>> {
        val fieldError = ex.bindingResult.fieldErrors.firstOrNull()
        val field = fieldError?.field
        val message = fieldError?.defaultMessage ?: "Validation failed"
        return ResponseEntity.badRequest().body(
            ApiResponse.error("VALIDATION_ERROR", message, field)
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(ex: Exception): ResponseEntity<ApiResponse<ErrorDetail>> {
        if (ex is ResponseStatusException) {
            val status = HttpStatus.resolve(ex.statusCode.value()) ?: HttpStatus.INTERNAL_SERVER_ERROR
            return ResponseEntity.status(status).body(
                ApiResponse.error("REQUEST_ERROR", ex.reason ?: status.reasonPhrase)
            )
        }
        log.error("Unhandled exception", ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ApiResponse.serverError("An unexpected error occurred")
        )
    }
}
