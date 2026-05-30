package com.example.foodie.user.internal;

import com.example.foodie.user.roletype.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

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
    private String address;

    @Enumerated(EnumType.STRING)
    private Role role = Role.CONSUMER;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();


}
