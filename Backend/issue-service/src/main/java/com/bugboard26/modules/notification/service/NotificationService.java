package com.bugboard26.modules.notification.service;

import com.bugboard26.shared.user.UserDirectory;
import com.bugboard26.shared.user.UserSummary;
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
import java.util.Map;
import java.util.stream.Collectors;

@Service 
@RequiredArgsConstructor 
public class NotificationService {
    private final NotificationRepository notificationRepository; 
    private final UserDirectory userDirectory; 

    public void notifyIssueResolved(String issueId, String issueTitolo, String reporterEmail, String resolverEmail) {
        if (reporterEmail.equals(resolverEmail)) {
            return; 
        }
        notificationRepository.save(Notification.builder()
                .recipientEmail(reporterEmail)
                .kind(NotificationKind.ISSUE_RESOLVED)
                .issueId(issueId)
                .issueTitolo(issueTitolo)
                .actorEmail(resolverEmail)
                .build()); 
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnread(String recipientEmail) {
        return 
        toResponses(notificationRepository.findByRecipientEmailAndReadAtIsNullOrderByCreatedAtDesc(recipientEmail));
    } 

    @Transactional(readOnly = true)
    public List<NotificationResponse> getHistory(String recipientEmail) {
        return 
        toResponses(notificationRepository.findTop50ByRecipientEmailOrderByCreatedAtDesc(recipientEmail));
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
        Map<String, UserSummary> actors = userDirectory.findByEmails(notifications.stream().map(Notification::getActorEmail)
                                                    .collect(Collectors.toSet())); 
        
        return notifications.stream().map(notification -> toResponse(notification, actors)).toList();
    }

    private NotificationResponse toResponse(Notification notification, Map<String, UserSummary> actors) {
        UserSummary actor = actors.get(notification.getActorEmail());
        return new NotificationResponse(
                notification.getId(),
                notification.getKind(),
                notification.getIssueId(),
                notification.getIssueTitolo(),
                notification.getActorEmail(),
                actor != null ? actor.nome() : null,
                actor != null ? actor.cognome() : null,
                notification.getCreatedAt(),
                notification.getReadAt());
    }
}
