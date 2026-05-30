package com.example.foodie.product.internal;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank
    private String name;

    private String description;

    @NotNull
    @Positive
    private BigDecimal price;

    private String unit; // e.g. "kg", "piece", "dozen"

    @NotNull
    private Integer stockQuantity = 0;

    @Enumerated(EnumType.STRING)
    private Category category;

    private boolean available = true;

    // Reference to farmer by ID only (no cross-module JPA join)
    @Column(nullable = false)
    private String farmerId;

    private String imageUrl;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}
