package com.example.event_service.controller;

import com.example.event_service.dto.CreateEventRequest;
import com.example.event_service.dto.EventResponse;
import com.example.event_service.dto.ReserveSeatsRequest;
import com.example.event_service.service.EventService;
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
@Tag(name = "Events", description = "API for managing events")
public class EventController {

    private final EventService eventService;

    @Operation(summary = "Get all events", description = "Public access, no authentication required")
    @ApiResponse(responseCode = "200", description = "List of events retrieved")
    @GetMapping("/public/events")
    public List<EventResponse> list() {
        return eventService.list();
    }

    @Operation(summary = "Get event by ID", description = "Public access, no authentication required")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Event found"),
        @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @GetMapping("/public/events/{id}")
    public EventResponse get(
            @Parameter(description = "Event ID", example = "1")
            @PathVariable Long id) {
        return eventService.getById(id);
    }

    @Operation(summary = "Create event", description = "Requires ADMIN role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Event created"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions (admin only)")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/api/admin/events")
    public EventResponse create(@Valid @RequestBody CreateEventRequest req) {
        return eventService.create(req);
    }
    
    @Operation(summary = "Update event", description = "Requires ADMIN role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Event updated"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions (admin only)"),
        @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/api/admin/events/{id}")
    public EventResponse update(
            @Parameter(description = "Event ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody CreateEventRequest req) {
        return eventService.update(id, req);
    }

    @Operation(summary = "Delete event", description = "Requires ADMIN role")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Event deleted"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions (admin only)"),
        @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/api/admin/events/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Event ID", example = "1")
            @PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Health check")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    @GetMapping("/public/health")
    public ResponseEntity<String> health() { 
        return ResponseEntity.ok("OK"); 
    }

    @Operation(summary = "Reserve seats")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Seats reserved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Event not found"),
        @ApiResponse(responseCode = "409", description = "Not enough seats available")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/api/events/{eventId}/reserve")
    public ResponseEntity<?> reserveSeats(
            @Parameter(description = "Event ID", example = "1")
            @PathVariable Long eventId,
            @Valid @RequestBody ReserveSeatsRequest request) {
        boolean success = eventService.reserveSeats(eventId, request.numberOfTickets(), request.bookingId());
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Not enough seats");
        }
    }

    @Operation(summary = "Cancel seat reservation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation cancelled"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/api/events/{eventId}/cancel-reservation")
    public ResponseEntity<?> cancelReservation(
            @Parameter(description = "Event ID", example = "1")
            @PathVariable Long eventId,
            @Valid @RequestBody ReserveSeatsRequest request) {
        eventService.cancelReservation(eventId, request.numberOfTickets(), request.bookingId());
        return ResponseEntity.ok().build();
    }

    
}