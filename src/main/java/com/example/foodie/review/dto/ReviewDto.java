package com.example.foodie.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class ReviewDto {

    public record CreateRequest(
        @NotBlank String userId,
        @NotBlank String productId,
        String farmerId,
        String orderId,
        @Min(1) @Max(5) int rating,
        String comment
    ) {}

    public record ReviewResponse(
        String id,
        String userId,
        String productId,
        String farmerId,
        int rating,
        String comment,
        LocalDateTime createdAt
    ) {}
}
