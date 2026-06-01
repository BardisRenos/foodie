package com.example.foodie.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;

    private static final int REFRESH_EXPIRY_DAYS = 7;

    @Transactional
    public String createRefreshToken(String email, String role, String userId) {
        // Delete any existing token for this user
        refreshTokenRepository.deleteByEmail(email);

        RefreshToken token = new RefreshToken();
        token.setToken(UUID.randomUUID().toString());
        token.setEmail(email);
        token.setRole(role);
        token.setUserId(userId);
        token.setExpiresAt(LocalDateTime.now().plusDays(REFRESH_EXPIRY_DAYS));
        refreshTokenRepository.save(token);

        return token.getToken();
    }

    @Transactional
    public AuthResponse refresh(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (token.isRevoked()) {
            throw new RuntimeException("Refresh token has been revoked");
        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token has expired — please log in again");
        }

        // Generate new access token
        String newAccessToken = jwtUtils.generateToken(
                token.getEmail(), token.getRole(), token.getUserId()
        );

        log.info("Access token refreshed for: {}", token.getEmail());
        return new AuthResponse(newAccessToken, token.getUserId(), token.getEmail(), token.getRole());
    }

    @Transactional
    public void revokeByEmail(String email) {
        refreshTokenRepository.deleteByEmail(email);
    }
}