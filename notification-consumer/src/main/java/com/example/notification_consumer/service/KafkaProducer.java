package com.example.notification_consumer.service;

import com.example.notification_consumer.event.NotificationSentEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendNotificationSent(NotificationSentEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("notification-events", payload);
            log.info("NotificationSent published to topic=notification-events for bookingId={}", event.getBookingId());
        } catch (Exception e) {
            log.error("Failed to serialize NotificationSentEvent: {}", e.getMessage(), e);
        }
    }
}
