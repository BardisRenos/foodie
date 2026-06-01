package com.example.foodie.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {

    public record OrderItemRequest(
        @NotBlank String productId,
        @NotBlank String productName,  // client sends this — no cross-module lookup needed
        @Positive int quantity
//        @Positive BigDecimal unitPrice // client sends current price at time of order
    ) {}

    public record PlaceOrderRequest(
        @NotBlank String userId,
        @NotBlank String farmerId,
        @NotEmpty List<OrderItemRequest> items,
        String deliveryAddress,
        String notes
    ) {}

    public record OrderItemResponse(
        String productId,
        String productName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
    ) {}

    public record OrderResponse(
        String id,
        String userId,
        String farmerId,
        String status,
        BigDecimal totalPrice,
        String deliveryAddress,
        String notes,
        List<OrderItemResponse> items,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {}
}
