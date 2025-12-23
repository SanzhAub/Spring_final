package com.example.notification_consumer.service;

import com.example.notification_consumer.entity.Notification;
import com.example.notification_consumer.event.BookingCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingEventsConsumer {

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    @KafkaListener(topics = "booking-events", groupId = "notification-group")
    public void consume(String message) {
        try {
            BookingCreatedEvent event =
                    objectMapper.readValue(message, BookingCreatedEvent.class);

            log.info("BOOKING EVENT received bookingId={}", event.getBookingId());
            log.error(">>> CREATE QUEUED CALLED <<<");
            Notification notification =
                    notificationService.createQueued(
                            event.getBookingId(),
                            event.getCustomerEmail()
                    );

            notificationService.sendNow(notification, false);

        } catch (Exception e) {
            log.error("Error processing booking event", e);
        }
    }
}