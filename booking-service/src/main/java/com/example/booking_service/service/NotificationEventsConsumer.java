package com.example.booking_service.service;

import com.example.booking_service.event.NotificationSentEvent;
import com.example.booking_service.model.BookingStatus;
import com.example.booking_service.repository.BookingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventsConsumer {

    private final ObjectMapper objectMapper;
    private final BookingRepository bookingRepository;

    @KafkaListener(topics = "notification-events", groupId = "booking-status-group")
    public void consume(String message) {
        try {
            log.info("Notification event received: {}", message);
            
            NotificationSentEvent evt = objectMapper.readValue(message, NotificationSentEvent.class);
            
            if (evt.getBookingId() == null) {
                log.warn("Event without bookingID: {}", message);
                return;
            }

            bookingRepository.findById(evt.getBookingId()).ifPresentOrElse(booking -> {
                if ("SENT".equalsIgnoreCase(evt.getStatus())) {
                    booking.setStatus(BookingStatus.NOTIFIED.name());
                    log.info("Booking {} updated to NOTIFIED", booking.getId());
                } else {
                    booking.setStatus(BookingStatus.NOTIFICATION_FAILED.name());
                    log.warn("Booking {} - notification has not been sent", booking.getId());
                }
                bookingRepository.save(booking);
            }, () -> log.warn("Booking {} not found", evt.getBookingId()));

        } catch (Exception e) {
            log.error("Notification-events processing error: {}", e.getMessage(), e);
        }
    }
}