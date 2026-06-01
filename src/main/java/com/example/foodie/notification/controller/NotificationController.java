package com.example.foodie.notification.controller;

import com.example.foodie.notification.internal.Notification;
import com.example.foodie.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('CONSUMER', 'FARMER', 'ADMIN')")
    public ResponseEntity<List<Notification>> list(@RequestParam String recipientId) {
        return ResponseEntity.ok(notificationService.findForRecipient(recipientId));
    }

    @GetMapping("/unread-count")
    @PreAuthorize("hasAnyRole('CONSUMER', 'FARMER', 'ADMIN')")
    public ResponseEntity<Map<String, Long>> unreadCount(@RequestParam String recipientId) {
        return ResponseEntity.ok(Map.of("count", notificationService.countUnread(recipientId)));
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('CONSUMER', 'FARMER', 'ADMIN')")
    public ResponseEntity<Void> markRead(@PathVariable String id) {
        notificationService.markRead(id);
        return ResponseEntity.ok().build();
    }
}
