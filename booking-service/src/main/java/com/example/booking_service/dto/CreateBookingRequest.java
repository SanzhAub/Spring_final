package com.example.booking_service.dto;

import jakarta.validation.constraints.*;

public record CreateBookingRequest(
        @NotNull Long eventId,
        @NotBlank String customerName,
        @NotBlank @Email String customerEmail,
        @NotNull @Min(1) Integer numberOfTickets
) {}
