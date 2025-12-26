package com.example.booking_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Booking data response")
public record BookingResponse(
    @Schema(description = "Booking ID", example = "1") Long id,
    @Schema(description = "Event ID", example = "1") Long eventId,
    @Schema(description = "Movie title", example = "Interstellar") String movieTitle,
    @Schema(description = "Event start time") LocalDateTime eventStartTime,
    @Schema(description = "Customer name", example = "John Doe") String customerName,
    @Schema(description = "Customer email", example = "john@example.com") String customerEmail,
    @Schema(description = "Number of tickets", example = "2") Integer numberOfTickets,
    @Schema(description = "Price per ticket", example = "500.00") BigDecimal pricePerTicket,
    @Schema(description = "Total price", example = "1000.00") BigDecimal totalPrice,
    @Schema(description = "Booking time") LocalDateTime bookingTime,
    @Schema(description = "Booking status", example = "CONFIRMED") String status
) {}