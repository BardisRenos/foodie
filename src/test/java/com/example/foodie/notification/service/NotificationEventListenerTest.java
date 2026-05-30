package com.example.foodie.notification.service;

import com.example.foodie.order.event.OrderPlacedEvent;
import com.example.foodie.order.event.OrderStatusChangedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationEventListenerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationEventListener notificationEventListener;

    @Test
    void onOrderPlaced_shouldNotifyFarmer() {
        OrderPlacedEvent event = new OrderPlacedEvent(
                "o1", "u1", "f1",
                List.of(new OrderPlacedEvent.OrderItem("p1", "Tomatoes", 2))
        );

        notificationEventListener.onOrderPlaced(event);

        verify(notificationService).send(
                eq("f1"),
                eq("FARMER"),
                eq("New order received!"),
                contains("o1"),
                eq("o1")
        );
    }

    @Test
    void onOrderPlaced_shouldNotifyConsumer() {
        OrderPlacedEvent event = new OrderPlacedEvent(
                "o1", "u1", "f1",
                List.of(new OrderPlacedEvent.OrderItem("p1", "Tomatoes", 2))
        );

        notificationEventListener.onOrderPlaced(event);

        verify(notificationService).send(
                eq("u1"),
                eq("USER"),
                eq("Order placed successfully"),
                contains("o1"),
                eq("o1")
        );
    }

    @Test
    void onOrderPlaced_shouldSendTwoNotifications_oneForFarmerOneForUser() {
        OrderPlacedEvent event = new OrderPlacedEvent(
                "o1", "u1", "f1",
                List.of(new OrderPlacedEvent.OrderItem("p1", "Tomatoes", 2))
        );

        notificationEventListener.onOrderPlaced(event);

        verify(notificationService, times(2)).send(any(), any(), any(), any(), any());
    }

    @Test
    void onOrderPlaced_shouldIncludeItemCountInFarmerMessage() {
        OrderPlacedEvent event = new OrderPlacedEvent(
                "o1", "u1", "f1",
                List.of(
                        new OrderPlacedEvent.OrderItem("p1", "Tomatoes", 2),
                        new OrderPlacedEvent.OrderItem("p2", "Cucumbers", 1),
                        new OrderPlacedEvent.OrderItem("p3", "Zucchini", 3)
                )
        );

        notificationEventListener.onOrderPlaced(event);

        verify(notificationService).send(
                eq("f1"),
                eq("FARMER"),
                eq("New order received!"),
                contains("3"),  // 3 items
                eq("o1")
        );
    }

    @Test
    void onOrderStatusChanged_shouldNotifyUser() {
        OrderStatusChangedEvent event = new OrderStatusChangedEvent(
                "o1", "u1", "f1", "CONFIRMED"
        );

        notificationEventListener.onOrderStatusChanged(event);

        verify(notificationService).send(
                eq("u1"),
                eq("USER"),
                eq("Order update: CONFIRMED"),
                contains("CONFIRMED"),
                eq("o1")
        );
    }

    @Test
    void onOrderStatusChanged_shouldIncludeOrderIdInMessage() {
        OrderStatusChangedEvent event = new OrderStatusChangedEvent(
                "o1", "u1", "f1", "SHIPPED"
        );

        notificationEventListener.onOrderStatusChanged(event);

        verify(notificationService).send(
                eq("u1"),
                eq("USER"),
                contains("SHIPPED"),
                contains("o1"),
                eq("o1")
        );
    }

    @Test
    void onOrderStatusChanged_shouldSendOnlyOneNotification() {
        OrderStatusChangedEvent event = new OrderStatusChangedEvent(
                "o1", "u1", "f1", "DELIVERED"
        );

        notificationEventListener.onOrderStatusChanged(event);

        verify(notificationService, times(1)).send(any(), any(), any(), any(), any());
    }

    @Test
    void onOrderStatusChanged_shouldHandleAllStatuses() {
        String[] statuses = {"PLACED", "CONFIRMED", "PREPARING", "SHIPPED", "DELIVERED", "CANCELLED"};

        for (String status : statuses) {
            OrderStatusChangedEvent event = new OrderStatusChangedEvent(
                    "o1", "u1", "f1", status
            );

            notificationEventListener.onOrderStatusChanged(event);

            verify(notificationService).send(
                    eq("u1"),
                    eq("USER"),
                    contains(status),
                    contains(status),
                    eq("o1")
            );

            reset(notificationService);
        }
    }
}