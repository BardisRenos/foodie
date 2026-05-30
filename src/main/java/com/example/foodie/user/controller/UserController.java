package com.example.foodie.user.controller;

import com.example.foodie.user.dto.UserDto;
import com.example.foodie.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto.UserResponse> register(@Valid @RequestBody UserDto.RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto.UserResponse> getUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.findById(id));
    }
}
