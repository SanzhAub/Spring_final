package com.example.notification_consumer.service;

import com.example.notification_consumer.entity.*;
import org.springframework.transaction.annotation.Transactional;
import com.example.notification_consumer.event.NotificationSentEvent;
import com.example.notification_consumer.repository.DeliveryAttemptRepository;
import com.example.notification_consumer.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final DeliveryAttemptRepository deliveryAttemptRepository;
    private final KafkaProducerService kafkaProducerService;

    /**
     * Создаём уведомление в статусе QUEUED
     */
    @Transactional
    public Notification createQueued(Long bookingId, String email) {
        log.error(">>> SAVING NOTIFICATION bookingId={} <<<", bookingId);
        Notification notification = Notification.builder()
                .bookingId(bookingId)
                .email(email)
                .status(NotificationStatus.QUEUED)
                .createdAt(LocalDateTime.now())
                .build();

        Notification saved = notificationRepository.save(notification);
        log.info("Notification QUEUED saved id={}", saved.getId());
        return saved;
    }

    /**
     * Пытаемся отправить уведомление
     */
    public void sendNow(Notification notification, boolean simulateFail) {
        DeliveryAttempt attempt = DeliveryAttempt.builder()
                .notification(notification)
                .retryCount(0)
                .attemptedAt(LocalDateTime.now())
                .build();

        try {
            if (simulateFail) {
                throw new RuntimeException("Simulated email failure");
            }

            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());

            notificationRepository.save(notification);

            NotificationSentEvent event = new NotificationSentEvent();
            event.setBookingId(notification.getBookingId());
            event.setCustomerEmail(notification.getEmail());
            event.setStatus("SENT");
            event.setSentAt(LocalDateTime.now());

            kafkaProducerService.publishNotificationSent(event);

            log.info("Notification SENT bookingId={}", notification.getBookingId());

        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setError(e.getMessage());
            notificationRepository.save(notification);

            attempt.setLastError(e.getMessage());
            log.error("Notification FAILED bookingId={}", notification.getBookingId(), e);
        }

        deliveryAttemptRepository.save(attempt);
    }
}