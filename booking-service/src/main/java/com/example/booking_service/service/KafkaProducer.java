package com.example.booking_service.service;

import com.example.booking_service.event.BookingCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendBookingEvent(BookingCreatedEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send("booking-events", json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize BookingCreatedEvent", e);
        }
    }
}
