package com.example.event_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Event data response")
public record EventResponse(
    @Schema(description = "Event ID", example = "1") Long id,
    @Schema(description = "Movie title", example = "Interstellar") String movieTitle,
    @Schema(description = "Event start time") LocalDateTime startTime,
    @Schema(description = "Price per ticket", example = "500.00") BigDecimal pricePerTicket,
    @Schema(description = "Number of available seats", example = "50") Integer availableSeats,
    @Schema(description = "List of seats") List<SeatResponse> seats
) {}