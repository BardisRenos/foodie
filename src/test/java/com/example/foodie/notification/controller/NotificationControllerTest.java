package com.example.foodie.notification.controller;

import com.example.foodie.notification.internal.Notification;
import com.example.foodie.notification.service.NotificationService;
import com.example.foodie.security.JwtAuthFilter;
import com.example.foodie.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = NotificationController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthFilter.class
        ))
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private JwtUtils jwtUtils;

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
    @WithMockUser(roles = "ADMIN")
    void list_shouldReturn200_withNotifications() throws Exception {
        when(notificationService.findForRecipient("u1")).thenReturn(List.of(notification));

        mockMvc.perform(get("/api/v1/notifications")
                        .param("recipientId", "u1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].recipientId").value("u1"))
                .andExpect(jsonPath("$[0].title").value("Order placed"))
                .andExpect(jsonPath("$[0].read").value(false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void list_shouldReturn200_withEmptyList_whenNoNotifications() throws Exception {
        when(notificationService.findForRecipient("unknown")).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/notifications")
                        .param("recipientId", "unknown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void unreadCount_shouldReturn200_withCount() throws Exception {
        when(notificationService.countUnread("u1")).thenReturn(3L);

        mockMvc.perform(get("/api/v1/notifications/unread-count")
                        .param("recipientId", "u1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(3));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void unreadCount_shouldReturn200_withZero_whenAllRead() throws Exception {
        when(notificationService.countUnread("u1")).thenReturn(0L);

        mockMvc.perform(get("/api/v1/notifications/unread-count")
                        .param("recipientId", "u1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void markRead_shouldReturn200_whenNotificationExists() throws Exception {
        doNothing().when(notificationService).markRead("n1");

        mockMvc.perform(patch("/api/v1/notifications/n1/read")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(notificationService).markRead("n1");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void markRead_shouldReturn200_whenNotificationNotFound() throws Exception {
        doNothing().when(notificationService).markRead("unknown");

        mockMvc.perform(patch("/api/v1/notifications/unknown/read")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(notificationService).markRead("unknown");
    }

    @Test
    void list_shouldReturn401_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/notifications")
                        .param("recipientId", "u1"))
                .andExpect(status().isUnauthorized());
    }
}