package com.example.notification_consumer.controller;

import com.example.notification_consumer.dto.NotificationResponse;
import com.example.notification_consumer.entity.Notification;
import com.example.notification_consumer.repository.NotificationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "API for managing notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    @Operation(summary = "Get notifications")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notifications retrieved"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/api/notifications")
    public List<NotificationResponse> find(
            @Parameter(description = "Booking ID for filtering", example = "1")
            @RequestParam(required = false) Long bookingId) {
        
        List<Notification> list = (bookingId == null)
                ? notificationRepository.findAll()
                : notificationRepository.findByBookingId(bookingId);

        return list.stream().map(this::toDto).toList();
    }

    @Operation(summary = "Get notification by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Notification found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/api/notifications/{id}")
    public NotificationResponse get(
            @Parameter(description = "Notification ID", example = "1")
            @PathVariable Long id) {
        
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + id));
        return toDto(n);
    }

    private NotificationResponse toDto(Notification n) {
        return new NotificationResponse(
                n.getId(), 
                n.getBookingId(), 
                n.getEmail(), 
                n.getStatus(),
                n.getCreatedAt(), 
                n.getSentAt(), 
                n.getError()
        );
    }
}