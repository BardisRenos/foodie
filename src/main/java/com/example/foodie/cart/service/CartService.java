package com.example.foodie.cart.service;

import com.example.foodie.cart.dto.CartDto;
import com.example.foodie.cart.internal.Cart;
import com.example.foodie.cart.internal.CartItem;
import com.example.foodie.cart.internal.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    public CartDto.CartResponse getCart(String userId) {
        Cart cart = getOrCreateCart(userId);
        return toResponse(cart);
    }

    @Transactional
    public CartDto.CartResponse addItem(String userId, CartDto.AddItemRequest request) {
        Cart cart = getOrCreateCart(userId);

        // Check if product already in cart — increase quantity
        Optional<CartItem> existing = cart.getItems().stream()
                .filter(i -> i.getProductId().equals(request.productId()))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + request.quantity());
        } else {
            CartItem item = new CartItem();
            item.setProductId(request.productId());
            item.setProductName(request.productName());
            item.setFarmerId(request.farmerId());
            item.setQuantity(request.quantity());
            item.setUnitPrice(request.unitPrice());
            item.setImageUrl(request.imageUrl());
            item.setUnit(request.unit());
            cart.getItems().add(item);
        }

        cart.setUpdatedAt(LocalDateTime.now());
        return toResponse(cartRepository.save(cart));
    }

    @Transactional
    public CartDto.CartResponse updateItemQuantity(String userId, String itemId, int quantity) {
        Cart cart = getOrCreateCart(userId);

        cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .ifPresent(i -> i.setQuantity(quantity));

        cart.setUpdatedAt(LocalDateTime.now());
        return toResponse(cartRepository.save(cart));
    }

    @Transactional
    public CartDto.CartResponse removeItem(String userId, String itemId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().removeIf(i -> i.getId().equals(itemId));
        cart.setUpdatedAt(LocalDateTime.now());
        return toResponse(cartRepository.save(cart));
    }

    @Transactional
    public void clearCart(String userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().clear();
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    public int getItemCount(String userId) {
        return getOrCreateCart(userId).getItems()
                .stream().mapToInt(CartItem::getQuantity).sum();
    }

    private Cart getOrCreateCart(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUserId(userId);
                    return cartRepository.save(cart);
                });
    }

    private CartDto.CartResponse toResponse(Cart cart) {
        List<CartDto.CartItemResponse> items = cart.getItems().stream().map(i -> {
            BigDecimal subtotal = i.getUnitPrice() != null
                    ? i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity()))
                    : BigDecimal.ZERO;
            return new CartDto.CartItemResponse(
                    i.getId(), i.getProductId(), i.getProductName(),
                    i.getFarmerId(), i.getQuantity(), i.getUnitPrice(),
                    subtotal, i.getImageUrl(), i.getUnit()
            );
        }).toList();

        BigDecimal total = items.stream()
                .map(CartDto.CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartDto.CartResponse(
                cart.getId(), cart.getUserId(), items, total,
                items.stream().mapToInt(CartDto.CartItemResponse::quantity).sum()
        );
    }
}