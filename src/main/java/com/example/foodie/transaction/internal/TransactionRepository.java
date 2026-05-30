package com.example.foodie.transaction.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByActorIdOrderByCreatedAtDesc(String actorId);
    List<Transaction> findByTypeOrderByCreatedAtDesc(TransactionType type);
}