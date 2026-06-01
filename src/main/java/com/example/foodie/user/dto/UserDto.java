package com.example.foodie.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserDto {

    public record RegisterRequest(
        @NotBlank String fullName,
        @Email @NotBlank String email,
        @NotBlank String password,
        String phone,
        String address
    ) {}

    public record UpdateRequest(
            String fullName,
            String phone,
            String address
    ) {}

    public record UserResponse(
        String id,
        String userId,
        String fullName,
        String email,
        String phone,
        String address,
        String role
    ) {}
}
