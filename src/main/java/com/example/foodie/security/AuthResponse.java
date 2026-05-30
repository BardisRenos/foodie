package com.example.foodie.security;

public record AuthResponse(
        String token,
        String id,
        String fullName,
        String role
) {}