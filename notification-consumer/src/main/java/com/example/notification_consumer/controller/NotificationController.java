package com.example.notification_consumer.api;

import com.example.notification_consumer.api.dto.NotificationResponse;
import com.example.notification_consumer.entity.Notification;
import com.example.notification_consumer.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;

    @GetMapping("/api/notifications")
    public List<NotificationResponse> find(@RequestParam(required = false) Long bookingId) {
        List<Notification> list = (bookingId == null)
                ? notificationRepository.findAll()
                : notificationRepository.findByBookingId(bookingId);

        return list.stream().map(this::toDto).toList();
    }

    @GetMapping("/api/notifications/{id}")
    public NotificationResponse get(@PathVariable Long id) {
        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + id));
        return toDto(n);
    }

    private NotificationResponse toDto(Notification n) {
        return new NotificationResponse(
                n.getId(), n.getBookingId(), n.getEmail(), n.getStatus(),
                n.getCreatedAt(), n.getSentAt(), n.getError()
        );
    }
}