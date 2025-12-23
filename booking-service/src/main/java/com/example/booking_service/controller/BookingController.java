package com.example.booking_service.controller;

import com.example.booking_service.dto.BookingResponse;
import com.example.booking_service.dto.CreateBookingRequest;
import com.example.booking_service.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/api/bookings")
    public BookingResponse create(@Valid @RequestBody CreateBookingRequest req) {
        return bookingService.createBooking(req);
    }

    @GetMapping("/api/bookings/{id}")
    public BookingResponse get(@PathVariable Long id) {
        return bookingService.getBooking(id);
    }

    @GetMapping("/api/bookings")
    public List<BookingResponse> list() {
        return bookingService.getAllBookings();
    }

    @DeleteMapping("/api/bookings/{id}")
    public void delete(@PathVariable Long id) {
        bookingService.deleteBooking(id);
    }

    @GetMapping("/public/health")
    public String health() { return "OK"; }

}
