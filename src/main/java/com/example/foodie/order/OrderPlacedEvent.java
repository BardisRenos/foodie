package com.example.foodie.order;

import java.util.List;

/**
 * Published when an order is successfully placed.
 * Other modules (product, notification) listen to this event
 * instead of being called directly — this is the Spring Modulith way.
 */
public record OrderPlacedEvent(
    String orderId,
    String userId,
    String farmerId,
    List<OrderItem> items
) {
    public record OrderItem(String productId, String productName, int quantity) {}
}
