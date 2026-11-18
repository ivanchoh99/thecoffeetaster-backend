package com.thecoffeetaster.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

/**
 * JWT token generation and validation provider.
 * 
 * This component handles all JWT operations for the authentication system:
 * - Access token generation (15-minute expiry)
 * - Refresh token generation (7-day expiry)
 * - Password reset token generation (1-hour expiry)
 * - Token validation and claims extraction
 * - Token hashing for secure storage
 * 
 * All JWT operations use HS256 algorithm with a shared secret key.
 * Token validation captures jjwt exceptions internally and returns optional/result types.
 * Callers should handle JwtException subtypes if needed for context-specific error handling.
 * 
 * Configuration via application.properties:
 * - jwt.secret: The signing key (environment variable in production)
 * - jwt.access-token-expiry: Access token TTL in seconds (default: 900 = 15 minutes)
 * - jwt.refresh-token-expiry: Refresh token TTL in seconds (default: 604800 = 7 days)
 */
@Component
public class JwtProvider {

    private final SecretKey signingKey;
    private final int accessTokenExpiry;
    private final int refreshTokenExpiry;
    private final int passwordResetTokenExpiry = 3600; // 1 hour in seconds

    /**
     * Initializes the JWT provider with signing key and configuration.
     * 
     * @param jwtSecret the secret key for HS256 signing (injected from jwt.secret property)
     * @param accessTokenExpiry TTL in seconds for access tokens (injected from jwt.access-token-expiry)
     * @param refreshTokenExpiry TTL in seconds for refresh tokens (injected from jwt.refresh-token-expiry)
     */
    public JwtProvider(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.access-token-expiry}") int accessTokenExpiry,
            @Value("${jwt.refresh-token-expiry}") int refreshTokenExpiry) {
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiry = accessTokenExpiry;
        this.refreshTokenExpiry = refreshTokenExpiry;
    }

    /**
     * Generates an access token for a user.
     * 
     * Access tokens are short-lived credentials (default 15 minutes) containing user identity
     * and role information for use in authenticated API requests.
     * 
     * Claims included:
     * - sub: User UUID as the subject
     * - email: User email address
     * - role: User role (e.g., ROLE_USER, ROLE_ADMIN)
     * - iat: Issued-at timestamp (epoch seconds)
     * - exp: Expiration timestamp (now + configured expiry seconds)
     * 
     * @param userId the user's UUID
     * @param email the user's email address
     * @param role the user's role
     * @return JWT token string signed with HS256
     */
    public String generateAccessToken(UUID userId, String email, String role) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(accessTokenExpiry);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("email", email)
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generates a refresh token for a user.
     * 
     * Refresh tokens are long-lived credentials (default 7 days) used to obtain new access tokens
     * without requiring the user to re-enter credentials. They are stored in the database and
     * can be revoked.
     * 
     * Claims included:
     * - sub: User UUID as the subject
     * - type: Token type marker ("refresh")
     * - iat: Issued-at timestamp (epoch seconds)
     * - exp: Expiration timestamp (now + configured expiry seconds)
     * 
     * @param userId the user's UUID
     * @return JWT token string signed with HS256
     */
    public String generateRefreshToken(UUID userId) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(refreshTokenExpiry);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("type", "refresh")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generates a password reset token for a user.
     * 
     * Password reset tokens are single-use, short-lived credentials (1 hour) sent via email
     * to allow users to reset forgotten passwords. They are stored in the database and marked
     * used after a successful reset.
     * 
     * Claims included:
     * - sub: User UUID as the subject
     * - type: Token type marker ("password_reset")
     * - iat: Issued-at timestamp (epoch seconds)
     * - exp: Expiration timestamp (now + 1 hour)
     * 
     * @param userId the user's UUID
     * @return JWT token string signed with HS256
     */
    public String generatePasswordResetToken(UUID userId) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(passwordResetTokenExpiry);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("type", "password_reset")
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates a JWT token and returns its claims.
     * 
     * This method performs two checks:
     * 1. Signature verification using the configured signing key
     * 2. Expiration timestamp validation
     * 
     * The method captures jjwt exceptions internally and communicates failures via return types,
     * allowing callers to wrap this in AuthResult patterns if needed.
     * 
     * @param token the JWT token string to validate
     * @return AuthResult containing Claims on success, or AuthError on failure
     *         - Success(Claims) if token is valid and not expired
     *         - Failure(TokenExpired) if token has expired
     *         - Failure(InvalidToken) if token is malformed or has invalid signature
     */
    public AuthResult<Claims> validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return new AuthResult.Success<>(claims);
        } catch (ExpiredJwtException e) {
            return new AuthResult.Failure<>(new AuthError.TokenExpired());
        } catch (MalformedJwtException | SignatureException e) {
            return new AuthResult.Failure<>(new AuthError.InvalidToken(e.getMessage()));
        } catch (JwtException e) {
            return new AuthResult.Failure<>(new AuthError.InvalidToken(e.getMessage()));
        }
    }

    /**
     * Extracts claims from a token without validating expiration.
     * 
     * This method only verifies the signature and parses the token structure.
     * It does not check if the token has expired, allowing callers to inspect
     * expired token content if needed (e.g., for deriving user context in error scenarios).
     * 
     * @param token the JWT token string to parse
     * @return Optional containing Claims if parsing succeeds, empty if parsing fails
     */
    protected Optional<Claims> getClaims(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return Optional.of(claims);
        } catch (JwtException e) {
            return Optional.empty();
        }
    }

    /**
     * Extracts the user UUID from a token's subject claim.
     * 
     * @param token the JWT token string
     * @return the user UUID
     * @throws IllegalArgumentException if token is invalid or subject cannot be parsed as UUID
     */
    public UUID extractUserUuid(String token) {
        Optional<Claims> claimsOpt = getClaims(token);
        if (claimsOpt.isEmpty()) {
            throw new IllegalArgumentException("Cannot extract UUID from invalid token");
        }
        try {
            return UUID.fromString(claimsOpt.get().getSubject());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Token subject is not a valid UUID", e);
        }
    }

    /**
     * Extracts the role claim from a token.
     * 
     * @param token the JWT token string
     * @return the role string, or null if not present in the token
     * @throws IllegalArgumentException if token is invalid
     */
    public String extractRole(String token) {
        Optional<Claims> claimsOpt = getClaims(token);
        if (claimsOpt.isEmpty()) {
            throw new IllegalArgumentException("Cannot extract role from invalid token");
        }
        return claimsOpt.get().get("role", String.class);
    }

    /**
     * Extracts the token type from a token.
     * 
     * Used to distinguish between different token purposes:
     * - "refresh": Refresh token for obtaining new access tokens
     * - "password_reset": One-time token for password reset operations
     * - null or absent: Implies an access token (default)
     * 
     * @param token the JWT token string
     * @return the token type string, or null if not present in the token
     * @throws IllegalArgumentException if token is invalid
     */
    public String extractTokenType(String token) {
        Optional<Claims> claimsOpt = getClaims(token);
        if (claimsOpt.isEmpty()) {
            throw new IllegalArgumentException("Cannot extract token type from invalid token");
        }
        return claimsOpt.get().get("type", String.class);
    }

    /**
     * Computes the SHA-256 hash of a token string.
     * 
     * Token hashes are stored in the database for revocation tracking and token reference
     * without storing the actual token material. This prevents database compromise from
     * immediately exposing all token values.
     * 
     * @param token the token string to hash
     * @return hexadecimal SHA-256 hash of the token
     */
    public String hashToken(String token) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
