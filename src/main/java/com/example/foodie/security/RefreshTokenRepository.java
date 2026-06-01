package com.example.foodie.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByEmail(String email);
    void deleteByUserId(String userId);
    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.expiresAt < :now")
    long deleteByExpiresAtBefore(@Param("now") LocalDateTime now);
}