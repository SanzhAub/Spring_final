package com.example.booking_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReserveSeatsRequest(
    @NotNull @Positive Integer numberOfTickets,
    @NotNull Long bookingId
) {}