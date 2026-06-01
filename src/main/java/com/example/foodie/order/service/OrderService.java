package com.example.foodie.order.service;

import com.example.foodie.order.dto.OrderDto;
import com.example.foodie.order.event.OrderCancelledEvent;
import com.example.foodie.order.event.OrderPlacedEvent;
import com.example.foodie.order.event.OrderStatusChangedEvent;
import com.example.foodie.order.internal.Order;
import com.example.foodie.order.internal.OrderItem;
import com.example.foodie.order.internal.OrderRepository;
import com.example.foodie.order.internal.OrderStatus;
import com.example.foodie.product.dto.ProductDto;
import com.example.foodie.product.service.ProductService;
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
    private final ProductService productService;

    @Transactional
    public OrderDto.OrderResponse placeOrder(OrderDto.PlaceOrderRequest request) {
        Order order = new Order();
        order.setUserId(request.userId());
        order.setFarmerId(request.farmerId());
        order.setDeliveryAddress(request.deliveryAddress());
        order.setNotes(request.notes());

        BigDecimal total = BigDecimal.ZERO;
        for (OrderDto.OrderItemRequest itemReq : request.items()) {

            ProductDto.ProductResponse product = productService
                    .validateAndGetProduct(itemReq.productId(), request.farmerId(), itemReq.quantity());

            BigDecimal subtotal = product.price()
                    .multiply(BigDecimal.valueOf(itemReq.quantity()));

            OrderItem item = new OrderItem();
            item.setProductId(product.id());
            item.setProductName(product.name());
            item.setQuantity(itemReq.quantity());
            item.setUnitPrice(product.price());
            item.setSubtotal(subtotal);
            total = total.add(subtotal);
            order.getItems().add(item);
        }

        order.setTotalPrice(total);
        Order saved = orderRepository.save(order);

        List<OrderPlacedEvent.OrderItem> eventItems = request.items().stream()
                .map(i -> new OrderPlacedEvent.OrderItem(i.productId(), i.quantity()))
                .toList();
        events.publishEvent(new OrderPlacedEvent(
                saved.getId(), saved.getUserId(), saved.getFarmerId(), eventItems));

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
        order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        Order saved = orderRepository.save(order);

        events.publishEvent(new OrderStatusChangedEvent(
                saved.getId(), saved.getUserId(), saved.getFarmerId(), saved.getStatus().name()
        ));

        return toResponse(saved);
    }

    public List<OrderDto.OrderResponse> findByUserAndStatus(String userId, String status) {
        OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase().trim());
        return orderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, orderStatus)
                .stream().map(this::toResponse).toList();
    }

    public List<OrderDto.OrderResponse> findByFarmerAndStatus(String farmerId, String status) {
        OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase().trim());
        return orderRepository.findByFarmerIdAndStatusOrderByCreatedAtDesc(farmerId, orderStatus)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public OrderDto.OrderResponse cancelOrder(String orderId, String actorId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        // Consumer can cancel their own order
        // Farmer can cancel an order assigned to them
        boolean isConsumer = order.getUserId().equals(actorId);
        boolean isFarmer = order.getFarmerId().equals(actorId);

        if (!isConsumer && !isFarmer) {
            throw new RuntimeException("You are not authorized to cancel this order");
        }

        // Consumer can only cancel PLACED orders
        if (isConsumer && order.getStatus() != OrderStatus.PLACED) {
            throw new IllegalStateException(
                    "Consumer can only cancel orders with status PLACED. Current status: "
                            + order.getStatus()
            );
        }

        // Farmer can cancel PLACED or CONFIRMED orders
        if (isFarmer && (order.getStatus() == OrderStatus.SHIPPED ||
                order.getStatus() == OrderStatus.DELIVERED ||
                order.getStatus() == OrderStatus.CANCELLED)) {
            throw new IllegalStateException(
                    "Farmer cannot cancel order with status: " + order.getStatus()
            );
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order saved = orderRepository.save(order);

        // Publish event — stock restored, notifications sent
        List<OrderCancelledEvent.OrderItem> eventItems = saved.getItems().stream()
                .map(i -> new OrderCancelledEvent.OrderItem(i.getProductId(), i.getQuantity()))
                .toList();
        events.publishEvent(new OrderCancelledEvent(
                saved.getId(), saved.getUserId(), saved.getFarmerId(), eventItems
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