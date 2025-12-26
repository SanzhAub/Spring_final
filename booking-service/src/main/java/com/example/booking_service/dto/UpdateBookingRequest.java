package com.example.booking_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Request to update a booking")
public record UpdateBookingRequest(
    @Schema(description = "Customer name", example = "John Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank String customerName,
    
    @Schema(description = "Customer email", example = "john@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank @Email String customerEmail,
    
    @Schema(description = "Number of tickets", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull @Min(1) Integer numberOfTickets
) {}