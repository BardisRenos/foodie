package com.example.foodie.security;

public record AuthResponse(
        String token,
        String refreshToken,
        String id,
        String fullName,
        String role
) {
    // Constructor without refreshToken for internal use
    public AuthResponse(String token, String id, String fullName, String role) {
        this(token, null, id, fullName, role);
    }
}