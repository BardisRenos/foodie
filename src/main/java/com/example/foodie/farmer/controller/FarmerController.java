package com.example.foodie.farmer.controller;

import com.example.foodie.farmer.dto.FarmerDto;
import com.example.foodie.farmer.service.FarmerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class FarmerController {

    private final FarmerService farmerService;

    @PostMapping("/auth/farmers/register")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<FarmerDto.FarmerResponse> register(@Valid @RequestBody FarmerDto.RegisterRequest request) {
        return ResponseEntity.ok(farmerService.register(request));
    }

    @GetMapping("/farmers/{id}")
    public ResponseEntity<FarmerDto.FarmerResponse> getFarmer(@PathVariable String id) {
        return ResponseEntity.ok(farmerService.findById(id));
    }

    @PutMapping("/farmers/{id}")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<FarmerDto.FarmerResponse> update(@PathVariable String id, @RequestBody FarmerDto.UpdateRequest request) {
        return ResponseEntity.ok(farmerService.update(id, request));
    }

    @GetMapping("/farmers")
    @PreAuthorize("hasAnyRole('CONSUMER', 'FARMER', 'ADMIN')")
    public ResponseEntity<List<FarmerDto.FarmerResponse>> listFarmers(@RequestParam(required = false) String location) {
        if (location != null) {
            return ResponseEntity.ok(farmerService.findByLocation(location));
        }
        return ResponseEntity.ok(farmerService.findVerified());
    }
}
