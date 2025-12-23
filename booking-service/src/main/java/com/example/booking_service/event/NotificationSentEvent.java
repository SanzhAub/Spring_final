package com.example.booking_service.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationSentEvent {
    private Long bookingId;
    private String customerEmail;
    private String status;
    private LocalDateTime sentAt;
}
