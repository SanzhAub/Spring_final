package com.example.booking_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
public class Booking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String movieTitle;
    
    private String customerName;
    
    private String customerEmail;
    
    private Integer numberOfTickets;
    
    private Double totalPrice;
    
    private LocalDateTime bookingTime = LocalDateTime.now();
    
    private String status = "CONFIRMED";
}
