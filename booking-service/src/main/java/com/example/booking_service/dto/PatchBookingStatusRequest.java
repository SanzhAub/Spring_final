package com.example.booking_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to change booking status")
public record PatchBookingStatusRequest(
    @Schema(description = "New status", example = "CONFIRMED", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank String status
) {}