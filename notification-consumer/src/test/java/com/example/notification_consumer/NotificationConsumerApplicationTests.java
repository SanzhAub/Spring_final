package com.example.notification_consumer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = "spring.kafka.listener.auto-startup=false")

@ActiveProfiles("test")
class NotificationConsumerApplicationTests {
    @Test void contextLoads() {}
}
