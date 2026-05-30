package com.example.foodie.notification.controller;

import com.example.foodie.notification.internal.Notification;
import com.example.foodie.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<Notification>> list(@RequestParam String recipientId) {
        return ResponseEntity.ok(notificationService.findForRecipient(recipientId));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> unreadCount(@RequestParam String recipientId) {
        return ResponseEntity.ok(Map.of("count", notificationService.countUnread(recipientId)));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markRead(@PathVariable String id) {
        notificationService.markRead(id);
        return ResponseEntity.ok().build();
    }
}
