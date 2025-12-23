package com.example.booking_service.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingCreatedEvent {
    private Long bookingId;
    private Long eventId;
    private String movieTitle;
    private String customerEmail;
    private Integer numberOfTickets;
    private LocalDateTime bookingTime;
}
