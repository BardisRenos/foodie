package com.example.foodie.address.dto;

import jakarta.validation.constraints.NotBlank;

public class AddressDto {

    public record CreateRequest(
            @NotBlank String fullName,
            @NotBlank String street,
            @NotBlank String city,
            String region,
            @NotBlank String zipCode,
            String country,
            String phone,
            boolean isDefault
    ) {}

    public record AddressResponse(
            String id,
            String userId,
            String fullName,
            String street,
            String city,
            String region,
            String zipCode,
            String country,
            String phone,
            boolean isDefault
    ) {}
}