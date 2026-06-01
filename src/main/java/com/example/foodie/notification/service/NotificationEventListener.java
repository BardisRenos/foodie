package com.example.foodie.notification.service;

import com.example.foodie.order.event.OrderCancelledEvent;
import com.example.foodie.order.event.OrderPlacedEvent;
import com.example.foodie.order.event.OrderStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @ApplicationModuleListener
    void onOrderPlaced(OrderPlacedEvent event) {
        notificationService.send(
                event.farmerId(), "FARMER",
                "New order received!",
                "You have a new order #" + event.orderId() + " with " + event.items().size() + " item(s).",
                event.orderId()
        );
        notificationService.send(
                event.userId(), "USER",
                "Order placed successfully",
                "Your order #" + event.orderId() + " has been placed and is awaiting confirmation.",
                event.orderId()
        );
    }

    @ApplicationModuleListener
    void onOrderStatusChanged(OrderStatusChangedEvent event) {
        String message = "Your order #" + event.orderId() + " status is now: " + event.newStatus();
        notificationService.send(
                event.userId(), "USER",
                "Order update: " + event.newStatus(),
                message,
                event.orderId()
        );
    }

    @ApplicationModuleListener
    void onOrderCancelled(OrderCancelledEvent event) {
        notificationService.send(
                event.farmerId(), "FARMER",
                "Order cancelled",
                "Order #" + event.orderId() + " has been cancelled by the consumer.",
                event.orderId()
        );
        notificationService.send(
                event.userId(), "USER",
                "Order cancelled successfully",
                "Your order #" + event.orderId() + " has been cancelled.",
                event.orderId()
        );
    }
}