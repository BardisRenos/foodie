package com.example.foodie.farmer.controller;

import com.example.foodie.farmer.dto.FarmerDto;
import com.example.foodie.farmer.service.FarmerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class FarmerController {

    private final FarmerService farmerService;

    public FarmerController(FarmerService farmerService) {
        this.farmerService = farmerService;
    }

    @PostMapping("/auth/farmers/register")
    public ResponseEntity<FarmerDto.FarmerResponse> register(@Valid @RequestBody FarmerDto.RegisterRequest request) {
        return ResponseEntity.ok(farmerService.register(request));
    }

    @GetMapping("/farmers/{id}")
    public ResponseEntity<FarmerDto.FarmerResponse> getFarmer(@PathVariable String id) {
        return ResponseEntity.ok(farmerService.findById(id));
    }

    @GetMapping("/farmers")
    public ResponseEntity<List<FarmerDto.FarmerResponse>> listFarmers(
            @RequestParam(required = false) String location) {
        if (location != null) {
            return ResponseEntity.ok(farmerService.findByLocation(location));
        }
        return ResponseEntity.ok(farmerService.findVerified());
    }
}
