package com.example.notification_consumer.service;

import com.example.notification_consumer.event.NotificationSentEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishNotificationSent(NotificationSentEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("notification-events", json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize NotificationSentEvent", e);
        }
    }
}
