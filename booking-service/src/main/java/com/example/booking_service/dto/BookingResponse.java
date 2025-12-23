package com.example.booking_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BookingResponse(
        Long id,
        Long eventId,
        String movieTitle,
        LocalDateTime eventStartTime,
        String customerName,
        String customerEmail,
        Integer numberOfTickets,
        BigDecimal pricePerTicket,
        BigDecimal totalPrice,
        LocalDateTime bookingTime,
        String status
) {}
