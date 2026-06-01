package com.example.foodie.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createBucket(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // Strict — 10 per minute — auth endpoints (prevent brute force)
        if (path.contains("/auth/login") || path.contains("/auth/forgot-password")) {
            return Bucket.builder()
                    .addLimit(Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1))))
                    .build();
        }

        // Medium — 30 per minute — order creation (prevent spam)
        if (path.contains("/orders") && method.equals("POST")) {
            return Bucket.builder()
                    .addLimit(Bandwidth.classic(30, Refill.greedy(30, Duration.ofMinutes(1))))
                    .build();
        }

        // Default — 200 per minute — general browsing
        return Bucket.builder()
                .addLimit(Bandwidth.classic(200, Refill.greedy(200, Duration.ofMinutes(1))))
                .build();
    }

    private String getBucketKey(HttpServletRequest request) {
        String ip = getClientIp(request);
        String path = request.getRequestURI();
        String method = request.getMethod();

        if (path.contains("/auth/login") || path.contains("/auth/forgot-password")) {
            return ip + ":auth";
        }
        if (path.contains("/orders") && method.equals("POST")) {
            return ip + ":orders";
        }
        return ip + ":general";
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String key = getBucketKey(request);
        Bucket bucket = buckets.computeIfAbsent(key, k -> createBucket(request));

        if (bucket.tryConsume(1)) {
            response.addHeader("X-Rate-Limit-Remaining",
                    String.valueOf(bucket.getAvailableTokens()));
            chain.doFilter(request, response);
        } else {
            log.warn("Rate limit exceeded for: {}", key);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("""
                {
                  "status": 429,
                  "message": "Too many requests. Please slow down and try again."
                }
                """);
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}