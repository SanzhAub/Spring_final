package com.example.event_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Request to reserve seats")
public record ReserveSeatsRequest(
    @Schema(description = "Number of seats", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull @Positive Integer numberOfTickets,
    
    @Schema(description = "Booking ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull Long bookingId
) {}