package com.thecoffeetaster.dto.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.thecoffeetaster.entity.Role
import java.time.Instant
import java.util.UUID

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    @get:JsonProperty("timestamp") val timestamp: String,
    @get:JsonProperty("status") val status: String,
    @get:JsonProperty("message") val message: String,
    @get:JsonProperty("data") val data: T?
) {
    companion object {
        fun <T> success(message: String, data: T?): ApiResponse<T> =
            ApiResponse(Instant.now().toString(), "success", message, data)

        fun error(code: String, message: String): ApiResponse<ErrorDetail> =
            ApiResponse(
                Instant.now().toString(),
                "client_error",
                message,
                ErrorDetail(code, message, null)
            )

        fun error(code: String, message: String, field: String?): ApiResponse<ErrorDetail> =
            ApiResponse(
                Instant.now().toString(),
                "client_error",
                message,
                ErrorDetail(code, message, field)
            )

        fun serverError(message: String): ApiResponse<ErrorDetail> =
            ApiResponse(Instant.now().toString(), "server_error", message, null)
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorDetail(
    @get:JsonProperty("code") val code: String,
    @get:JsonProperty("message") val message: String,
    @get:JsonProperty("field") val field: String?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserProfile(
    @get:JsonProperty("id") val id: UUID,
    @get:JsonProperty("email") val email: String,
    @get:JsonProperty("displayName") val displayName: String,
    @get:JsonProperty("role") val role: Role,
    @get:JsonProperty("createdAt") val createdAt: Instant
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LoginResponse(
    @get:JsonProperty("user") val user: UserProfile,
    @get:JsonProperty("accessToken") val accessToken: String
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class RefreshResponse(
    @get:JsonProperty("accessToken") val accessToken: String
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CurrentUserResponse(
    @get:JsonProperty("user") val user: UserProfile
)
