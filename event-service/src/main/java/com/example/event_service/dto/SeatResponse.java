package com.example.event_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Seat data response")
public record SeatResponse(
    @Schema(description = "Seat ID", example = "1") Long id,
    @Schema(description = "Seat number", example = "1") Integer seatNumber,
    @Schema(description = "Seat status", example = "AVAILABLE") String status
) {}
