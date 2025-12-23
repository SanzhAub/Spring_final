package com.example.notification_consumer.repository;

import com.example.notification_consumer.entity.DeliveryAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryAttemptRepository extends JpaRepository<DeliveryAttempt, Long> {}