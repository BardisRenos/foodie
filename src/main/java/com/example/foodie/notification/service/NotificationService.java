package com.example.foodie.notification.service;

import com.example.foodie.notification.internal.Notification;
import com.example.foodie.notification.internal.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Async
    public void send(String recipientId, String recipientType, String title, String message, String orderId) {
        Notification n = new Notification();
        n.setRecipientId(recipientId);
        n.setRecipientType(recipientType);
        n.setTitle(title);
        n.setMessage(message);
        n.setRelatedOrderId(orderId);
        notificationRepository.save(n);
    }

    public List<Notification> findForRecipient(String recipientId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId);
    }

    public long countUnread(String recipientId) {
        return notificationRepository.countByRecipientIdAndReadFalse(recipientId);
    }

    public void markRead(String notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }
}
