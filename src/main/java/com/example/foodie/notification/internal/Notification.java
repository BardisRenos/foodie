package com.example.foodie.notification.internal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String recipientId;   // userId or farmerId

    private String recipientType; // "USER" or "FARMER"
    private String title;
    private String message;
    private boolean read = false;
    private String relatedOrderId;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}
