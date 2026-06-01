package com.example.foodie.payment.controller;

import com.example.foodie.payment.dto.PaymentDto;
import com.example.foodie.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-intent")
    @PreAuthorize("hasRole('CONSUMER')")
    public ResponseEntity<PaymentDto.PaymentResponse> createIntent(@Valid @RequestBody PaymentDto.CreatePaymentRequest request) {
        return ResponseEntity.ok(paymentService.createPaymentIntent(request));
    }

    @PostMapping("/refund")
    @PreAuthorize("hasAnyRole('ADMIN', 'FARMER')")
    public ResponseEntity<PaymentDto.PaymentResponse> refund(@Valid @RequestBody PaymentDto.RefundRequest request) {
        return ResponseEntity.ok(paymentService.refund(request));
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        paymentService.handleWebhook(payload, sigHeader);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('CONSUMER', 'FARMER', 'ADMIN')")
    public ResponseEntity<PaymentDto.PaymentResponse> getByOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(paymentService.findByOrder(orderId));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('CONSUMER', 'ADMIN')")
    public ResponseEntity<List<PaymentDto.PaymentResponse>> getByUser(@PathVariable String userId) {
        return ResponseEntity.ok(paymentService.findByUser(userId));
    }
}