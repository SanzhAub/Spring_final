package com.example.event_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = EventApplication.class)
@ActiveProfiles("test")
class EventApplicationTests {

    @Test
    void contextLoads() {}
}