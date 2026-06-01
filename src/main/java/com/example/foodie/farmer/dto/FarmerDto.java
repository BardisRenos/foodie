package com.example.foodie.farmer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class FarmerDto {

    public record RegisterRequest(
        @NotBlank String fullName,
        @Email @NotBlank String email,
        @NotBlank String password,
        String phone,
        @NotBlank String farmName,
        String farmLocation,
        String description
    ) {}

    public record UpdateRequest(
            String fullName,
            String phone,
            String farmName,
            String farmLocation,
            String description
    ) {}

    public record FarmerResponse(
        String id,
        String farmerId,
        String fullName,
        String email,
        String phone,
        String farmName,
        String farmLocation,
        String description,
        boolean verified
    ) {}
}
