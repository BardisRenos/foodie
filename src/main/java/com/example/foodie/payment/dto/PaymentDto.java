package com.example.foodie.payment.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDto {

    public record CreatePaymentRequest(
            @NotBlank String orderId,
            @NotBlank String userId
    ) {}

    public record RefundRequest(
            @NotBlank String orderId,
            String reason
    ) {}

    public record PaymentResponse(
            String id,
            String orderId,
            String userId,
            String clientSecret,
            String stripePaymentIntentId,
            String status,
            BigDecimal amount,
            String currency,
            LocalDateTime createdAt
    ) {}
}