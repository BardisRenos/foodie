package com.example.foodie.transaction.service;

import com.example.foodie.order.event.OrderCancelledEvent;
import com.example.foodie.order.event.OrderPlacedEvent;
import com.example.foodie.order.event.OrderStatusChangedEvent;
import com.example.foodie.transaction.internal.ActorType;
import com.example.foodie.transaction.internal.TransactionStatus;
import com.example.foodie.transaction.internal.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionEventListener {

    private final TransactionService transactionService;

    @ApplicationModuleListener
    void onOrderPlaced(OrderPlacedEvent event) {
        transactionService.record(
                event.userId(), ActorType.USER,
                TransactionType.PURCHASE,
                event.orderId(),
                "Order placed with " + event.items().size() + " item(s)",
                TransactionStatus.SUCCESS
        );
    }

    @ApplicationModuleListener
    void onOrderStatusChanged(OrderStatusChangedEvent event) {
        transactionService.record(
                event.farmerId(), ActorType.FARMER,
                TransactionType.ORDER_STATUS_CHANGE,
                event.orderId(),
                "Order status changed to " + event.newStatus(),
                TransactionStatus.SUCCESS
        );
    }

    @ApplicationModuleListener
    void onOrderCancelled(OrderCancelledEvent event) {
        transactionService.record(
                event.userId(), ActorType.USER,
                TransactionType.ORDER_STATUS_CHANGE,
                event.orderId(),
                "Order cancelled",
                TransactionStatus.SUCCESS
        );
    }
}