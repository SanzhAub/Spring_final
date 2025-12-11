package com.example.booking_service.service;
import com.example.booking_service.event.BookingCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    public void sendBookingEvent(BookingCreatedEvent event) {
        kafkaTemplate.send("booking-events", event);
        System.out.println("Sent event to Kafka: " + event.getBookingId());
    }
}