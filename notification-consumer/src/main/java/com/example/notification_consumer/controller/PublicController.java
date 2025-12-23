package com.example.notification_consumer.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicController {

    @GetMapping("/public/health")
    public String health() {
        return "OK";
    }
}