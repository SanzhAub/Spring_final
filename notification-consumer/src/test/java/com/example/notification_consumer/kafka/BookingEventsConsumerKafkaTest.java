package com.example.notification_consumer.kafka;

import com.example.notification_consumer.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;


import static org.mockito.Mockito.*;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = "booking-events")
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "spring.kafka.listener.auto-startup=true"
})
class BookingEventsConsumerKafkaTest {

    @Autowired KafkaTemplate<String, String> kafkaTemplate;

    @MockBean NotificationService notificationService;

    @Test
    void whenBookingEventPublished_consumerCallsCreateQueued() {
        String json = """
          {"bookingId":99,"eventId":1,"movieTitle":"X","customerName":"A","customerEmail":"a@b.com",
           "numberOfTickets":1,"totalPrice":100.00,"bookingTime":"2025-12-27T12:00:00"}
        """;

        kafkaTemplate.send("booking-events", json);

        org.awaitility.Awaitility.await()
                .untilAsserted(() ->
                        verify(notificationService).createQueued(99L, "a@b.com")
                );
    }
}