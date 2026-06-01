package com.example.foodie.transaction.controller;

import com.example.foodie.transaction.internal.Transaction;
import com.example.foodie.transaction.internal.TransactionType;
import com.example.foodie.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Transaction>> list(
            @RequestParam(required = false) String actorId,
            @RequestParam(required = false) TransactionType type) {
        if (actorId != null) return ResponseEntity.ok(transactionService.findByActor(actorId));
        if (type != null) return ResponseEntity.ok(transactionService.findByType(type));
        return ResponseEntity.badRequest().build();
    }
}