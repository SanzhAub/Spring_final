package com.example.notification_consumer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicController {

    @Operation(summary = "Health check")
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    @GetMapping("/public/health")
    public String health() {
        return "OK";
    }
}