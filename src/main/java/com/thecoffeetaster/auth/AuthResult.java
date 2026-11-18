package com.thecoffeetaster.auth;

/**
 * Sealed result type for authentication operations.
 * 
 * Replaces exception-based error handling with explicit result types.
 * All AuthService methods return AuthResult<T> — expected auth failures are
 * returned as Failure(AuthError), never thrown as exceptions.
 * 
 * The compiler enforces exhaustiveness when switching over AuthResult variants.
 * 
 * @param <T> The type of data contained in a successful result
 */
public sealed interface AuthResult<T> permits AuthResult.Success, AuthResult.Failure {

    /**
     * Represents a successful authentication operation.
     * Contains the result data of type T.
     */
    record Success<T>(T value) implements AuthResult<T> {
        public Success {
            if (value == null) {
                throw new IllegalArgumentException("Success value cannot be null");
            }
        }
    }

    /**
     * Represents a failed authentication operation.
     * Contains an AuthError describing the failure reason.
     */
    record Failure<T>(AuthError error) implements AuthResult<T> {
        public Failure {
            if (error == null) {
                throw new IllegalArgumentException("AuthError cannot be null");
            }
        }
    }
}
