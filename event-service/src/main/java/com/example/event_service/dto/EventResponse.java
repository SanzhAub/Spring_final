package com.example.event_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventResponse(
        Long id,
        String movieTitle,
        LocalDateTime startTime,
        BigDecimal pricePerTicket,
        Integer availableSeats
) {}
