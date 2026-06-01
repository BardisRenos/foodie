package com.example.foodie.order.service;

import com.example.foodie.order.internal.OrderRepository;
import com.example.foodie.order.internal.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderQueryService {

    private final OrderRepository orderRepository;

    public boolean hasActiveOrdersForProduct(String productId) {
        return orderRepository.existsByItemsProductIdAndStatusIn(
                productId,
                List.of(OrderStatus.PLACED, OrderStatus.CONFIRMED,
                        OrderStatus.PREPARING, OrderStatus.SHIPPED)
        );
    }

    public boolean isOrderDelivered(String orderId) {
        return orderRepository.findById(orderId)
                .map(o -> o.getStatus() == OrderStatus.DELIVERED)
                .orElse(false);
    }

    public boolean isOrderOwner(String orderId, String userId) {
        return orderRepository.findById(orderId)
                .map(o -> o.getUserId().equals(userId))
                .orElse(false);
    }
}