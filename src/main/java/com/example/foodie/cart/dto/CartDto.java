package com.example.foodie.cart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

public class CartDto {

    public record AddItemRequest(
            @NotBlank String productId,
            @NotBlank String productName,
            @NotBlank String farmerId,
            @Positive int quantity,
            BigDecimal unitPrice,
            String imageUrl,
            String unit
    ) {}

    public record UpdateQuantityRequest(
            @Positive int quantity
    ) {}

    public record CartItemResponse(
            String id,
            String productId,
            String productName,
            String farmerId,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal subtotal,
            String imageUrl,
            String unit
    ) {}

    public record CartResponse(
            String id,
            String userId,
            List<CartItemResponse> items,
            BigDecimal total,
            int itemCount
    ) {}
}