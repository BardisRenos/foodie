package com.example.foodie.user.controller;

import com.example.foodie.user.dto.UserDto;
import com.example.foodie.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
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
}
