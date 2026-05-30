package com.example.foodie.order.service;

import com.example.foodie.order.event.OrderPlacedEvent;
import com.example.foodie.order.event.OrderStatusChangedEvent;
import com.example.foodie.order.dto.OrderDto;
import com.example.foodie.order.internal.Order;
import com.example.foodie.order.internal.OrderItem;
import com.example.foodie.order.internal.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher events;


    @Transactional
    public OrderDto.OrderResponse placeOrder(OrderDto.PlaceOrderRequest request) {
        Order order = new Order();
        order.setUserId(request.userId());
        order.setFarmerId(request.farmerId());
        order.setDeliveryAddress(request.deliveryAddress());
        order.setNotes(request.notes());

        // Build order items from request — no cross-module call here
        BigDecimal total = BigDecimal.ZERO;
        for (OrderDto.OrderItemRequest itemReq : request.items()) {
            OrderItem item = new OrderItem();
            item.setProductId(itemReq.productId());
            item.setProductName(itemReq.productName());
            item.setQuantity(itemReq.quantity());
            item.setUnitPrice(itemReq.unitPrice());
            BigDecimal subtotal = itemReq.unitPrice().multiply(BigDecimal.valueOf(itemReq.quantity()));
            item.setSubtotal(subtotal);
            total = total.add(subtotal);
            order.getItems().add(item);
        }
        order.setTotalPrice(total);
        Order saved = orderRepository.save(order);

        // Publish event — product module will deduct stock, notification module will alert farmer
        List<OrderPlacedEvent.OrderItem> eventItems = request.items().stream()
            .map(i -> new OrderPlacedEvent.OrderItem(i.productId(), i.productName(), i.quantity()))
            .toList();
        events.publishEvent(new OrderPlacedEvent(saved.getId(), saved.getUserId(), saved.getFarmerId(), eventItems));

        return toResponse(saved);
    }

    public OrderDto.OrderResponse findById(String id) {
        return orderRepository.findById(id)
            .map(this::toResponse)
            .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }

    public List<OrderDto.OrderResponse> findByUser(String userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
            .stream().map(this::toResponse).toList();
    }

    public List<OrderDto.OrderResponse> findByFarmer(String farmerId) {
        return orderRepository.findByFarmerIdOrderByCreatedAtDesc(farmerId)
            .stream().map(this::toResponse).toList();
    }

    @Transactional
    public OrderDto.OrderResponse updateStatus(String orderId, String status) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        order.setStatus(Order.OrderStatus.valueOf(status.toUpperCase()));
        Order saved = orderRepository.save(order);

        // Publish event — notification module will alert the consumer
        events.publishEvent(new OrderStatusChangedEvent(
            saved.getId(), saved.getUserId(), saved.getFarmerId(), saved.getStatus().name()
        ));

        return toResponse(saved);
    }

    private OrderDto.OrderResponse toResponse(Order o) {
        List<OrderDto.OrderItemResponse> items = o.getItems().stream().map(i ->
            new OrderDto.OrderItemResponse(i.getProductId(), i.getProductName(),
                i.getQuantity(), i.getUnitPrice(), i.getSubtotal())
        ).toList();
        return new OrderDto.OrderResponse(
            o.getId(), o.getUserId(), o.getFarmerId(), o.getStatus().name(),
            o.getTotalPrice(), o.getDeliveryAddress(), o.getNotes(),
            items, o.getCreatedAt(), o.getUpdatedAt()
        );
    }
}
