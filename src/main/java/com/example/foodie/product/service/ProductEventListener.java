package com.example.foodie.product.service;

import com.example.foodie.order.event.OrderPlacedEvent;
import com.example.foodie.product.internal.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Listens for events published by the order module.
 * This is how Spring Modulith modules communicate —
 * product module never imports anything from order module's internals.
 */
@Component
@RequiredArgsConstructor
public class ProductEventListener {

    private final ProductRepository productRepository;

    @ApplicationModuleListener
    void onOrderPlaced(OrderPlacedEvent event) {
        for (OrderPlacedEvent.OrderItem item : event.items()) {
            productRepository.findById(item.productId()).ifPresent(product -> {
                int newStock = product.getStockQuantity() - item.quantity();
                product.setStockQuantity(Math.max(newStock, 0));
                if (newStock <= 0) product.setAvailable(false);
                productRepository.save(product);
            });
        }
    }
}
