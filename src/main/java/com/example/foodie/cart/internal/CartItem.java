package com.example.foodie.cart.internal;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
@Getter @Setter
@NoArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String productId;

    private String productName;
    private String farmerId;
    private BigDecimal unitPrice;
    private String imageUrl;
    private int quantity;
    private String unit;
}