package com.example.foodie.order.event;

/**
 * Published when an order status changes.
 * The notification module listens and sends alerts to user and farmer.
 */
public record OrderStatusChangedEvent(
    String orderId,
    String userId,
    String farmerId,
    String newStatus
) {}
