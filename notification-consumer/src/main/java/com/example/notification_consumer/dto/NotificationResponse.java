package com.example.notification_consumer.dto;

import com.example.notification_consumer.entity.NotificationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Notification data response")
public record NotificationResponse(
    @Schema(description = "Notification ID", example = "1") Long id,
    @Schema(description = "Booking ID", example = "1") Long bookingId,
    @Schema(description = "Customer email", example = "john@example.com") String email,
    @Schema(description = "Notification status") NotificationStatus status,
    @Schema(description = "Creation date") LocalDateTime createdAt,
    @Schema(description = "Sent date") LocalDateTime sentAt,
    @Schema(description = "Error message", example = "Connection timeout") String error
) {}