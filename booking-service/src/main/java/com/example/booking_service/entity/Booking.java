package com.example.booking_service.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Reference to event-service event */
    @Column(nullable = false)
    private Long eventId;

    /** Snapshot fields (so booking stays consistent even if event changes later) */
    @Column(name = "movie_title", nullable = false)
    private String movieTitle;

    @Column(name = "event_start_time", nullable = false)
    private LocalDateTime eventStartTime;

    @Column(name = "price_per_ticket", precision = 10, scale = 2)
    private BigDecimal pricePerTicket;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @Column(name = "number_of_tickets", nullable = false)
    private Integer numberOfTickets;

    @Column(name="total_price", precision=12, scale=2)
    private BigDecimal totalPrice;

    @Column(name = "booking_time", nullable = false)
    private LocalDateTime bookingTime = LocalDateTime.now();

    @Column(nullable = false)
    private String status;
}
