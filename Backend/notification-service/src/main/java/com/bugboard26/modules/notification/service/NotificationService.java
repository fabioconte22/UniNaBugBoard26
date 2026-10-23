package com.bugboard26.modules.notification.service;

import com.bugboard26.common.messaging.IssueResolvedMessage;
import com.bugboard26.modules.notification.dto.NotificationResponse;
import com.bugboard26.modules.notification.model.Notification;
import com.bugboard26.modules.notification.model.NotificationKind;
import com.bugboard26.modules.notification.repository.NotificationRepository;
import com.bugboard26.shared.exception.NotificationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public void notifyIssueResolved(IssueResolvedMessage message) {
        // Chi risolve una issue che aveva segnalato lui stesso non ha bisogno di essere avvisato.
        if (message.reporterEmail().equals(message.resolverEmail())) {
            return;
        }
        notificationRepository.save(Notification.builder()
                .recipientEmail(message.reporterEmail())
                .kind(NotificationKind.ISSUE_RESOLVED)
                .issueId(message.issueId())
                .issueTitolo(message.issueTitolo())
                .actorEmail(message.resolverEmail())
                .actorNome(message.resolverNome())
                .actorCognome(message.resolverCognome())
                .build());
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnread(String recipientEmail) {
        return toResponses(notificationRepository.findByRecipientEmailAndReadAtIsNullOrderByCreatedAtDesc(recipientEmail));
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getHistory(String recipientEmail) {
        return toResponses(notificationRepository.findTop50ByRecipientEmailOrderByCreatedAtDesc(recipientEmail));
    }

    public void markAsRead(String notificationId, String recipientEmail) {
        Notification notification = notificationRepository.findByIdAndRecipientEmail(notificationId, recipientEmail)
                .orElseThrow(NotificationNotFoundException::new);

        if (notification.getReadAt() == null) {
            notification.setReadAt(Instant.now());
            notificationRepository.save(notification);
        }
    }

    private List<NotificationResponse> toResponses(List<Notification> notifications) {
        return notifications.stream().map(this::toResponse).toList();
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getKind(),
                notification.getIssueId(),
                notification.getIssueTitolo(),
                notification.getActorEmail(),
                notification.getActorNome(),
                notification.getActorCognome(),
                notification.getCreatedAt(),
                notification.getReadAt());
    }
}