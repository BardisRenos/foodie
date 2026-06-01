package com.example.foodie.product.event;

import com.example.foodie.order.event.OrderCancelledEvent;
import com.example.foodie.order.event.OrderPlacedEvent;
import com.example.foodie.product.internal.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Listens for events published by the order module.
 * This is how Spring Modulith modules communicate —
 * product module never imports anything from order module's internals.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ProductEventListener {

    private final ProductRepository productRepository;

    @ApplicationModuleListener
    void onOrderCancelled(OrderCancelledEvent event) {
        for (OrderCancelledEvent.OrderItem item : event.items()) {
            productRepository.findById(item.productId()).ifPresent(product -> {
                product.setStockQuantity(product.getStockQuantity() + item.quantity());
                product.setAvailable(true);
                productRepository.save(product);
                log.info("Stock restored for product: {} quantity: {}",
                        product.getName(), item.quantity());
            });
        }
    }
}
