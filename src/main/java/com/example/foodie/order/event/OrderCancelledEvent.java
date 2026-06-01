package com.example.foodie.order.event;

import java.util.List;

public record OrderCancelledEvent(
        String orderId,
        String userId,
        String farmerId,
        List<OrderItem> items
) {
    public record OrderItem(String productId, int quantity) {}
}