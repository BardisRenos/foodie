package com.example.foodie.order.controller;

import com.example.foodie.order.dto.OrderDto;
import com.example.foodie.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderDto.OrderResponse> place(@Valid @RequestBody OrderDto.PlaceOrderRequest request) {
        return ResponseEntity.ok(orderService.placeOrder(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto.OrderResponse> getOrder(@PathVariable String id) {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<OrderDto.OrderResponse>> list(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String farmerId) {
        if (userId != null) return ResponseEntity.ok(orderService.findByUser(userId));
        if (farmerId != null) return ResponseEntity.ok(orderService.findByFarmer(farmerId));
        return ResponseEntity.badRequest().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderDto.OrderResponse> updateStatus(
            @PathVariable String id, @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }
}
