package com.example.event_service.repository;

import com.example.event_service.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByEventId(Long eventId);
    List<Seat> findByEventIdAndStatus(Long eventId, String status);
    List<Seat> findByBookingId(Long bookingId);
}