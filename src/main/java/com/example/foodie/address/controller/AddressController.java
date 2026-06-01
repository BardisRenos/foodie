package com.example.foodie.address.controller;

import com.example.foodie.address.dto.AddressDto;
import com.example.foodie.address.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/{userId}/addresses")
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<List<AddressDto.AddressResponse>> list(@PathVariable String userId) {
        return ResponseEntity.ok(addressService.findByUser(userId));
    }

    @PostMapping
    public ResponseEntity<AddressDto.AddressResponse> create(@PathVariable String userId, @Valid @RequestBody AddressDto.CreateRequest request) {
        return ResponseEntity.ok(addressService.create(userId, request));
    }

    @PatchMapping("/{addressId}/default")
    public ResponseEntity<AddressDto.AddressResponse> setDefault(@PathVariable String userId, @PathVariable String addressId) {
        return ResponseEntity.ok(addressService.setDefault(userId, addressId));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> delete(@PathVariable String userId, @PathVariable String addressId) {
        addressService.delete(userId, addressId);
        return ResponseEntity.noContent().build();
    }
}