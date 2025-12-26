package com.example.notification_consumer.service;

import com.example.notification_consumer.entity.*;
import org.springframework.transaction.annotation.Transactional;
import com.example.notification_consumer.event.NotificationSentEvent;
import com.example.notification_consumer.repository.DeliveryAttemptRepository;
import com.example.notification_consumer.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
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
     * Создаём уведомление в статусе QUEUED и сразу отправляем
     */
    @Transactional
    public void createQueued(Long bookingId, String email) {
        log.info("Creating a notification for bookingID={}", bookingId);
        
        Notification notification = Notification.builder()
                .bookingId(bookingId)
                .email(email)
                .status(NotificationStatus.QUEUED)
                .createdAt(LocalDateTime.now())
                .build();

        Notification saved = notificationRepository.save(notification);
        log.info("Notification QUEUED created by id={}", saved.getId());
        
        // Отправляем уведомление (симуляция)
        sendNow(saved);
    }

    /**
     * Пытаемся отправить уведомление
     */
    @Async
    public void sendNow(Notification notification) {
        DeliveryAttempt attempt = DeliveryAttempt.builder()
                .notification(notification)
                .retryCount(0)
                .attemptedAt(LocalDateTime.now())
                .build();

        try {
            // Симуляция отправки email (всегда успешно)
            Thread.sleep(1000); // Имитация задержки
            
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notificationRepository.save(notification);

            // Отправляем событие в Kafka
            NotificationSentEvent event = new NotificationSentEvent();
            event.setBookingId(notification.getBookingId());
            event.setCustomerEmail(notification.getEmail());
            event.setStatus("SENT");
            event.setSentAt(LocalDateTime.now());

            kafkaProducerService.publishNotificationSent(event);

            log.info("Notification sent by BookingID={}", notification.getBookingId());

        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setError(e.getMessage());
            notificationRepository.save(notification);

            attempt.setLastError(e.getMessage());
            log.error("The notification was not sent BookingID={}", notification.getBookingId(), e);
        }

        deliveryAttemptRepository.save(attempt);
    }
}