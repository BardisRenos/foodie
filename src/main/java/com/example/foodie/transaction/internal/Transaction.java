package com.example.foodie.transaction.internal;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter @Setter
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String actorId;         // userId or farmerId

    @Enumerated(EnumType.STRING)
    private ActorType actorType;    // USER or FARMER

    @Enumerated(EnumType.STRING)
    private TransactionType type;   // LOGIN, LOGOUT, PURCHASE, ORDER_STATUS_CHANGE...

    private String relatedEntityId; // orderId, productId, etc.
    private String description;
    private String ipAddress;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status; // SUCCESS, FAILED

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}