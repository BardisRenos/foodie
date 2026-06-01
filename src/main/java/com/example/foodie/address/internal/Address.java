package com.example.foodie.address.internal;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "addresses")
@Getter @Setter
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String userId;

    private String fullName;
    private String street;
    private String city;
    private String region;
    private String zipCode;
    private String country = "Greece";
    private String phone;
    private boolean isDefault = false;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}