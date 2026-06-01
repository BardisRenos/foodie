package com.example.foodie.cart.controller;

import com.example.foodie.cart.dto.CartDto;
import com.example.foodie.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('CONSUMER')")
    public ResponseEntity<CartDto.CartResponse> getCart(@PathVariable String userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/{userId}/items")
    @PreAuthorize("hasRole('CONSUMER')")
    public ResponseEntity<CartDto.CartResponse> addItem(@PathVariable String userId, @Valid @RequestBody CartDto.AddItemRequest request) {
        return ResponseEntity.ok(cartService.addItem(userId, request));
    }

    @PatchMapping("/{userId}/items/{itemId}")
    @PreAuthorize("hasRole('CONSUMER')")
    public ResponseEntity<CartDto.CartResponse> updateQuantity(@PathVariable String userId, @PathVariable String itemId, @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.updateItemQuantity(userId, itemId, quantity));
    }

    @DeleteMapping("/{userId}/items/{itemId}")
    @PreAuthorize("hasRole('CONSUMER')")
    public ResponseEntity<CartDto.CartResponse> removeItem(@PathVariable String userId, @PathVariable String itemId) {
        return ResponseEntity.ok(cartService.removeItem(userId, itemId));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('CONSUMER')")
    public ResponseEntity<Void> clearCart(@PathVariable String userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}/count")
    @PreAuthorize("hasRole('CONSUMER')")
    public ResponseEntity<Integer> getItemCount(@PathVariable String userId) {
        return ResponseEntity.ok(cartService.getItemCount(userId));
    }
}