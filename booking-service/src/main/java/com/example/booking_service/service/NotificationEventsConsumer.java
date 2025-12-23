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
            NotificationSentEvent evt = objectMapper.readValue(message, NotificationSentEvent.class);
            if (evt.getBookingId() == null) {
                log.warn("notification-events received without bookingId: {}", message);
                return;
            }

            bookingRepository.findById(evt.getBookingId()).ifPresentOrElse(booking -> {
                if ("SENT".equalsIgnoreCase(evt.getStatus())) {
                    booking.setStatus(BookingStatus.NOTIFIED.name());
                } else {
                    booking.setStatus(BookingStatus.NOTIFICATION_FAILED.name());
                }
                bookingRepository.save(booking);
                log.info("Booking {} status updated to {}", booking.getId(), booking.getStatus());
            }, () -> log.warn("Booking {} not found while processing NotificationSentEvent", evt.getBookingId()));

        } catch (Exception e) {
            log.error("Failed to process notification-events message: {}", message, e);
        }
    }
}
