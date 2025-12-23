package com.example.notification_consumer.api.dto;

import com.example.notification_consumer.entity.NotificationStatus;
import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        Long bookingId,
        String email,
        NotificationStatus status,
        LocalDateTime createdAt,
        LocalDateTime sentAt,
        String error
) {}