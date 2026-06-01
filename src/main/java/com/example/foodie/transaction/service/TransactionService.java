package com.example.foodie.transaction.service;

import com.example.foodie.transaction.internal.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Async
    public void record(String actorId, ActorType actorType,
                       TransactionType type, String relatedEntityId,
                       String description, TransactionStatus status) {
        Transaction t = new Transaction();
        t.setActorId(actorId);
        t.setActorType(actorType);
        t.setType(type);
        t.setRelatedEntityId(relatedEntityId);
        t.setDescription(description);
        t.setStatus(status);
        transactionRepository.save(t);
        log.info("[TRANSACTION] {} - {} - {} - {}", actorType, actorId, type, status);
    }

    public List<Transaction> findByActor(String actorId) {
        return transactionRepository.findByActorIdOrderByCreatedAtDesc(actorId);
    }

    public List<Transaction> findByType(TransactionType type) {
        return transactionRepository.findByTypeOrderByCreatedAtDesc(type);
    }
}