package com.thecoffeetaster.dto.response;

import java.time.Instant;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thecoffeetaster.entity.Role;

/**
 * Response Data Transfer Objects for the Authentication API.
 * <p>
 * This class contains sealed record types for all authentication-related responses.
 * Records use {@code @JsonProperty} for camelCase JSON serialization and
 * {@code @JsonInclude(JsonInclude.Include.NON_NULL)} to omit null fields.
 * </p>
 *
 * @since 1.0
 */
public final class ResponseDTOs {

    private ResponseDTOs() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Standard API response envelope for all endpoints.
     * <p>
     * All `/api/v1/auth/*` endpoints return responses wrapped in this envelope,
     * containing a timestamp, status, message, and optional data payload.
     * </p>
     *
     * @param <T> the type of data in the response
     * @since 1.0
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ApiResponse<T>(
            @JsonProperty("timestamp")
            String timestamp,

            @JsonProperty("status")
            String status,

            @JsonProperty("message")
            String message,

            @JsonProperty("data")
            T data
    ) {
        /**
         * Factory method for successful responses.
         *
         * @param message the success message
         * @param data the response data
         * @param <T> the type of data
         * @return a success ApiResponse with current timestamp and status="success"
         */
        public static <T> ApiResponse<T> success(String message, T data) {
            return new ApiResponse<>(
                    Instant.now().toString(),
                    "success",
                    message,
                    data
            );
        }

        /**
         * Factory method for error responses.
         *
         * @param code the error code (e.g., "INVALID_CREDENTIALS")
         * @param message the error message
         * @param <T> the type of data (typically ErrorDetail or null)
         * @return an error ApiResponse with current timestamp and status="client_error" or "server_error"
         */
        public static <T> ApiResponse<T> error(String code, String message) {
            return new ApiResponse<>(
                    Instant.now().toString(),
                    "client_error",
                    message,
                    (T) new ErrorDetail(code, message, null)
            );
        }

        /**
         * Factory method for error responses with field information.
         *
         * @param code the error code
         * @param message the error message
         * @param field the field name that caused the error (nullable)
         * @param <T> the type of data
         * @return an error ApiResponse
         */
        public static <T> ApiResponse<T> error(String code, String message, String field) {
            return new ApiResponse<>(
                    Instant.now().toString(),
                    "client_error",
                    message,
                    (T) new ErrorDetail(code, message, field)
            );
        }

        /**
         * Factory method for server error responses.
         *
         * @param message the error message
         * @param <T> the type of data
         * @return an error ApiResponse with status="server_error"
         */
        public static <T> ApiResponse<T> serverError(String message) {
            return new ApiResponse<>(
                    Instant.now().toString(),
                    "server_error",
                    message,
                    null
            );
        }
    }

    /**
     * Error detail record included in error API responses.
     * <p>
     * Contains a machine-readable error code, human-readable message,
     * and optional field name for validation errors.
     * </p>
     *
     * @since 1.0
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ErrorDetail(
            @JsonProperty("code")
            String code,

            @JsonProperty("message")
            String message,

            @JsonProperty("field")
            String field
    ) {
    }

    /**
     * User profile information returned in authentication responses.
     * <p>
     * Contains immutable user information: UUID, email, display name,
     * role, and creation timestamp.
     * </p>
     *
     * @since 1.0
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record UserProfile(
            @JsonProperty("id")
            UUID id,

            @JsonProperty("email")
            String email,

            @JsonProperty("displayName")
            String displayName,

            @JsonProperty("role")
            Role role,

            @JsonProperty("createdAt")
            Instant createdAt
    ) {
    }

    /**
     * Response data for login and registration endpoints.
     * <p>
     * Contains the authenticated user's profile and a fresh access token.
     * The refresh token is issued as an HttpOnly cookie by the server.
     * </p>
     *
     * @since 1.0
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record LoginResponse(
            @JsonProperty("user")
            UserProfile user,

            @JsonProperty("accessToken")
            String accessToken
    ) {
    }

    /**
     * Response data for token refresh endpoint.
     * <p>
     * Contains only the new access token. A new refresh token is issued
     * as an HttpOnly cookie using the sliding window strategy.
     * </p>
     *
     * @since 1.0
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record RefreshResponse(
            @JsonProperty("accessToken")
            String accessToken
    ) {
    }

    /**
     * Response data for the /me (get current user) endpoint.
     * <p>
     * Returns the authenticated user's profile information.
     * </p>
     *
     * @since 1.0
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CurrentUserResponse(
            @JsonProperty("user")
            UserProfile user
    ) {
    }
}
