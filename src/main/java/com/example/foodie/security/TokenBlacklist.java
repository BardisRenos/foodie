package com.example.foodie.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenBlacklist {

    private final RefreshTokenRepository refreshTokenRepository;

    // Store blacklisted tokens for 24h (same as JWT expiration)
    private final Cache<String, Boolean> blacklisted = Caffeine.newBuilder()
            .expireAfterWrite(24, TimeUnit.HOURS)
            .maximumSize(10_000)
            .build();

    public void blacklist(String token) {
        blacklisted.put(token, true);
    }

    public boolean isBlacklisted(String token) {
        return blacklisted.getIfPresent(token) != null;
    }

    @Scheduled(fixedRate = 3600000) // every hour
    public void logBlacklistSize() {
        log.info("Token blacklist size: {}", blacklisted.estimatedSize());
    }

    @Scheduled(cron = "0 0 2 * * *") // every day at 2am
    @Transactional
    public void cleanupExpiredTokens() {
        long deleted = refreshTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
        log.info("Cleaned up {} expired refresh tokens", deleted);
    }
}