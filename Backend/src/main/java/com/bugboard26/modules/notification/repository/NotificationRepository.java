package com.bugboard26.modules.notification.repository;

import com.bugboard26.modules.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findByRecipientEmailAndReadAtIsNullOrderByCreatedAtDesc(String recipientEmail);

    List<Notification> findTop50ByRecipientEmailOrderByCreatedAtDesc(String recipientEmail);

    Optional<Notification> findByIdAndRecipientEmail(String id, String recipientEmail);

}
