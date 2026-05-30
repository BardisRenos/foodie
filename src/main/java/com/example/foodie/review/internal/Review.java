package com.example.foodie.review.internal;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String productId;

    private String farmerId;
    private String orderId;

    @Min(1) @Max(5)
    private int rating;

    private String comment;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}
