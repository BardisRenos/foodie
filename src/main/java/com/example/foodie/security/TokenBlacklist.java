package com.example.foodie.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

@Component
public class TokenBlacklist {

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
}