package com.example.booking_service.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventResponse(
        Long id,
        String movieTitle,
        LocalDateTime startTime,
        BigDecimal pricePerTicket,
        Integer availableSeats
) {}
