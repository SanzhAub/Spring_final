package com.example.event_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Request to create an event")
public record CreateEventRequest(
    @Schema(description = "Movie title", example = "Interstellar", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank String movieTitle,
    
    @Schema(description = "Start time", example = "2025-12-25T20:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull LocalDateTime startTime,
    
    @Schema(description = "Price per ticket", example = "500.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull @Positive BigDecimal pricePerTicket,
    
    @Schema(description = "Number of available seats", example = "50", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull @Min(0) Integer availableSeats
) {}