package com.example.notification_consumer.repository;

import com.example.notification_consumer.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByBookingId(Long bookingId);
}