package com.example.notification_consumer.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationSentEvent {
    private Long bookingId;

    // как минимум одно поле для адресата
    private String customerEmail;

    // SENT / FAILED (или QUEUED / SENT / FAILED)
    private String status;

    // когда реально "отправили"
    private LocalDateTime sentAt;

    // если упало — причина
    private String error;
}