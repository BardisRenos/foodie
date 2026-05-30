package com.example.foodie.product.dto;

import com.example.foodie.product.internal.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class ProductDto {

    public record CreateRequest(
        @NotBlank String name,
        String description,
        @NotNull @Positive BigDecimal price,
        String unit,
        @NotNull Integer stockQuantity,
        Category category,
        String imageUrl,
        @NotBlank String farmerId
    ) {}

    public record ProductResponse(
        String id,
        String name,
        String description,
        BigDecimal price,
        String unit,
        Integer stockQuantity,
        String category,
        boolean available,
        String farmerId,
        String imageUrl
    ) {}
}
