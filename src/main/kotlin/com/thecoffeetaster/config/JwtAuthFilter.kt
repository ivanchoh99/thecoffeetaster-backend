package com.thecoffeetaster.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.thecoffeetaster.auth.AuthResult
import com.thecoffeetaster.auth.JwtProvider
import com.thecoffeetaster.dto.response.ApiResponse
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.UUID

/**
 * Validates the `Authorization: Bearer <jwt>` header and populates the
 * SecurityContext with the user's public UUID and role.
 */
@Component
class JwtAuthFilter(
    private val jwtProvider: JwtProvider,
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(JwtAuthFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header = request.getHeader("Authorization")
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = header.removePrefix("Bearer ").trim()
        when (val validated = jwtProvider.validateToken(token)) {
            is AuthResult.Failure -> {
                log.debug("Rejected JWT: {}", validated.error)
                writeUnauthorized(response)
                return
            }
            is AuthResult.Success -> {
                val claims = validated.value
                if (claims == null) {
                    writeUnauthorized(response)
                    return
                }
                val userUuid = runCatching { UUID.fromString(claims.subject) }.getOrNull()
                if (userUuid == null) {
                    writeUnauthorized(response)
                    return
                }
                val role = claims.get("role", String::class.java)
                val authorities = if (role != null) {
                    listOf(SimpleGrantedAuthority(role))
                } else {
                    emptyList()
                }
                val authentication = UsernamePasswordAuthenticationToken(userUuid, null, authorities)
                val context = SecurityContextHolder.createEmptyContext()
                context.authentication = authentication
                SecurityContextHolder.setContext(context)
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun writeUnauthorized(response: HttpServletResponse) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        objectMapper.writeValue(
            response.writer,
            ApiResponse.error("UNAUTHORIZED", "Unauthorized")
        )
    }
}
