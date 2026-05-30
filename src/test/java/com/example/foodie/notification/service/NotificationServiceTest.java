package com.example.foodie.notification.service;

import com.example.foodie.notification.internal.Notification;
import com.example.foodie.notification.internal.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private Notification notification;

    @BeforeEach
    void setUp() {
        notification = new Notification();
        notification.setRecipientId("u1");
        notification.setRecipientType("USER");
        notification.setTitle("Order placed");
        notification.setMessage("Your order has been placed");
        notification.setRelatedOrderId("o1");
        notification.setRead(false);
    }

    @Test
    void send_shouldSaveNotification() {
        notificationService.send("u1", "USER", "Order placed",
                "Your order has been placed", "o1");

        verify(notificationRepository).save(argThat(n ->
                n.getRecipientId().equals("u1") &&
                        n.getRecipientType().equals("USER") &&
                        n.getTitle().equals("Order placed") &&
                        n.getMessage().equals("Your order has been placed") &&
                        n.getRelatedOrderId().equals("o1")
        ));
    }

    @Test
    void send_shouldSetReadFalseByDefault() {
        notificationService.send("u1", "USER", "Test", "Test message", "o1");

        verify(notificationRepository).save(argThat(n -> !n.isRead()));
    }

    @Test
    void findForRecipient_shouldReturnNotifications() {
        when(notificationRepository.findByRecipientIdOrderByCreatedAtDesc("u1"))
                .thenReturn(List.of(notification));

        List<Notification> result = notificationService.findForRecipient("u1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRecipientId()).isEqualTo("u1");
        assertThat(result.get(0).getTitle()).isEqualTo("Order placed");
    }

    @Test
    void findForRecipient_shouldReturnEmpty_whenNoNotifications() {
        when(notificationRepository.findByRecipientIdOrderByCreatedAtDesc("unknown"))
                .thenReturn(List.of());

        List<Notification> result = notificationService.findForRecipient("unknown");

        assertThat(result).isEmpty();
    }

    @Test
    void countUnread_shouldReturnCorrectCount() {
        when(notificationRepository.countByRecipientIdAndReadFalse("u1")).thenReturn(3L);

        long count = notificationService.countUnread("u1");

        assertThat(count).isEqualTo(3L);
    }

    @Test
    void countUnread_shouldReturnZero_whenAllRead() {
        when(notificationRepository.countByRecipientIdAndReadFalse("u1")).thenReturn(0L);

        long count = notificationService.countUnread("u1");

        assertThat(count).isZero();
    }

    @Test
    void markRead_shouldSetReadTrue_whenNotificationExists() {
        when(notificationRepository.findById("n1")).thenReturn(Optional.of(notification));

        notificationService.markRead("n1");

        assertThat(notification.isRead()).isTrue();
        verify(notificationRepository).save(notification);
    }

    @Test
    void markRead_shouldDoNothing_whenNotificationNotFound() {
        when(notificationRepository.findById("unknown")).thenReturn(Optional.empty());

        notificationService.markRead("unknown");

        verify(notificationRepository, never()).save(any());
    }
}