package com.example.foodie.farmer.internal;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "farmers", indexes = {
        @Index(name = "idx_farmer_email", columnList = "email"),
        @Index(name = "idx_farmer_location", columnList = "farmLocation"),
        @Index(name = "idx_farmer_verified", columnList = "verified")
})
public class Farmer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank
    private String fullName;

    @Email
    @NotBlank
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    private String password;

    private String phone;

    @NotBlank
    private String farmName;

    private String farmLocation;
    private String description;
    private boolean verified = false;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}
