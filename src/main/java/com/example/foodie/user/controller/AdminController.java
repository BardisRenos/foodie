package com.example.foodie.user.controller;

import com.example.foodie.farmer.dto.FarmerDto;
import com.example.foodie.farmer.service.FarmerService;
import com.example.foodie.user.dto.UserDto;
import com.example.foodie.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final FarmerService farmerService;

    @GetMapping("/users")
    public ResponseEntity<List<UserDto.UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/farmers")
    public ResponseEntity<List<FarmerDto.FarmerResponse>> getAllFarmers() {
        return ResponseEntity.ok(farmerService.findAll());
    }

    @PatchMapping("/farmers/{id}/verify")
    public ResponseEntity<FarmerDto.FarmerResponse> verifyFarmer(@PathVariable String id) {
        return ResponseEntity.ok(farmerService.verify(id));
    }

    @PatchMapping("/farmers/{id}/unverify")
    public ResponseEntity<FarmerDto.FarmerResponse> unverifyFarmer(@PathVariable String id) {
        return ResponseEntity.ok(farmerService.unverify(id));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/farmers/{id}")
    public ResponseEntity<Void> deleteFarmer(@PathVariable String id) {
        farmerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}