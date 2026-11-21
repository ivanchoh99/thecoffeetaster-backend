package com.thecoffeetaster.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.thecoffeetaster.dto.response.ApiResponse
import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Duration
import java.util.concurrent.TimeUnit

/**
 * Per-IP rate limiting for POST /api/v1/auth/login (Bucket4j + Caffeine).
 */
@Component
class RateLimitFilter(
    private val objectMapper: ObjectMapper,
    @Value("\${auth.rate-limit.requests-per-minute:10}") private val requestsPerMinute: Long
) : OncePerRequestFilter() {

    private val buckets: Cache<String, Bucket> = Caffeine.newBuilder()
        .expireAfterAccess(1, TimeUnit.HOURS)
        .maximumSize(100_000)
        .build()

    override fun shouldNotFilter(request: HttpServletRequest): Boolean =
        !(request.method == "POST" && request.requestURI == "/api/v1/auth/login")

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val clientIp = request.remoteAddr ?: "unknown"
        val bucket = buckets.get(clientIp) {
            Bucket.builder()
                .addLimit(
                    Bandwidth.builder()
                        .capacity(requestsPerMinute)
                        .refillGreedy(requestsPerMinute, Duration.ofMinutes(1))
                        .build()
                )
                .build()
        }

        if (bucket != null && bucket.tryConsume(1)) {
            filterChain.doFilter(request, response)
            return
        }

        response.status = 429
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        objectMapper.writeValue(
            response.writer,
            ApiResponse.error("RATE_LIMITED", "Too many requests. Please try again later.")
        )
    }
}
