package com.example.booking_service.event;

import lombok.Data;

import java.math.BigDecimal;  // ДОБАВЛЯЕМ ИМПОРТ
import java.time.LocalDateTime;

@Data
public class BookingCreatedEvent {
    private Long bookingId;
    private Long eventId;
    private String movieTitle;
    private String customerName;    // ДОБАВЛЯЕМ
    private String customerEmail;
    private Integer numberOfTickets;
    private BigDecimal totalPrice;  // ДОБАВЛЯЕМ
    private LocalDateTime bookingTime;
}