package com.example.event_service.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateEventRequest(
        @NotBlank String movieTitle,
        @NotNull LocalDateTime startTime,
        @NotNull @Positive BigDecimal pricePerTicket,
        @NotNull @Min(0) Integer availableSeats
) {}
