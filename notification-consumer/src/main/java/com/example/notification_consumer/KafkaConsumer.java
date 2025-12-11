package com.example.notification_consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class KafkaConsumer {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @KafkaListener(topics = "booking-events", groupId = "notification-group")
    public void consume(String message) {
        try {
            Map<String, Object> event = objectMapper.readValue(message, Map.class);
            
            log.info("===== KAFKA EVENT RECEIVED =====");
            log.info("Booking ID: {}", event.get("bookingId"));
            log.info("Movie: {}", event.get("movieTitle"));
            log.info("Customer: {}", event.get("customerName"));
            log.info("Email: {}", event.get("customerEmail"));
            log.info("Tickets: {}", event.get("numberOfTickets"));
            log.info("Total: ${}", event.get("totalPrice"));
            log.info("Booking created successfully!");
            log.info("==================================");
            
        } catch (Exception e) {
            log.error("Error parsing Kafka message: {}", e.getMessage());
            System.out.println("📨 Raw message: " + message);
        }
    }
}