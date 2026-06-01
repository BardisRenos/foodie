package com.example.foodie.user.controller;

import com.example.foodie.user.dto.UserDto;
import com.example.foodie.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@PreAuthorize("hasRole('CONSUMER')")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDto.UserResponse> register(@Valid @RequestBody UserDto.RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto.UserResponse> getUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserDto.UserResponse> update(@PathVariable String id, @RequestBody UserDto.UpdateRequest request) {
        return ResponseEntity.ok(userService.update(id, request));
    }
}
