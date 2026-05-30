package com.example.foodie.notification.service;

import com.example.foodie.order.OrderPlacedEvent;
import com.example.foodie.order.OrderStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Listens for order events and sends notifications to the right people.
 * Notification module depends on public events only — never on order internals.
 */
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @ApplicationModuleListener
    void onOrderPlaced(OrderPlacedEvent event) {
        // Notify the farmer about the new order
        notificationService.send(
            event.farmerId(), "FARMER",
            "New order received!",
            "You have a new order #" + event.orderId() + " with " + event.items().size() + " item(s).",
            event.orderId()
        );
        // Notify the consumer that the order was placed
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
}
