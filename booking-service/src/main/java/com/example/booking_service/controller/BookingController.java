package com.example.booking_service.controller;

import com.example.booking_service.dto.BookingResponse;
import com.example.booking_service.dto.CreateBookingRequest;
import com.example.booking_service.dto.PatchBookingStatusRequest;
import com.example.booking_service.dto.UpdateBookingRequest;
import com.example.booking_service.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Tag(name = "Bookings", description = "API for managing bookings")
public class BookingController {

    private final BookingService bookingService;

    @Operation(
        summary = "Create a booking",
        description = "Creates a new booking for an event with seat reservation"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Booking created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Event not found"),
        @ApiResponse(responseCode = "409", description = "Not enough seats available")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/api/bookings")
    public BookingResponse create(@Valid @RequestBody CreateBookingRequest req) {
        return bookingService.createBooking(req);
    }

    @Operation(summary = "Get booking by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Booking found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/api/bookings/{id}")
    public BookingResponse get(
            @Parameter(description = "Booking ID", example = "1")
            @PathVariable Long id) {
        return bookingService.getBooking(id);
    }

    @Operation(summary = "Get all bookings")
    @ApiResponse(responseCode = "200", description = "Success")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/api/bookings")
    public List<BookingResponse> list() {
        return bookingService.getAllBookings();
    }

    @Operation(summary = "Delete booking")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Booking deleted"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/api/bookings/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Booking ID", example = "1")
            @PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Health check")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    @GetMapping("/public/health")
    public ResponseEntity<String> health() { 
        return ResponseEntity.ok("OK"); 
    }

    @Operation(summary = "Update booking")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Booking updated"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/api/bookings/{id}")
    public BookingResponse update(
            @Parameter(description = "Booking ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookingRequest req) {
        return bookingService.updateBooking(id, req);
    }

    @Operation(summary = "Update booking status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status updated"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/api/bookings/{id}/status")
    public BookingResponse patchStatus(
            @Parameter(description = "Booking ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody PatchBookingStatusRequest req) {
        return bookingService.updateStatus(id, req.status());
    }

    @Operation(summary = "Cancel booking")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Booking cancelled"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/api/bookings/{id}/cancel")
    public ResponseEntity<Void> cancel(
            @Parameter(description = "Booking ID", example = "1")
            @PathVariable Long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.ok().build();
    }
}