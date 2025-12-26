package com.example.notification_consumer.event;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookingCreatedEvent {
    private Long bookingId;
    private Long eventId;
    private String movieTitle;
    private String customerName;    // ДОБАВЛЯЕМ ЭТО ПОЛЕ
    private String customerEmail;
    private Integer numberOfTickets;
    private BigDecimal totalPrice;  
    private LocalDateTime bookingTime;
}