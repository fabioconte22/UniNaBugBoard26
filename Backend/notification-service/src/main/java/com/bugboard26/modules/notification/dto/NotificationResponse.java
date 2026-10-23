package com.bugboard26.modules.notification.dto;

import com.bugboard26.modules.notification.model.NotificationKind;

import java.time.Instant;

public record NotificationResponse(
        String id, 
        NotificationKind kind, 
        String issueId,
        String issueTitolo, 
        String actorEmail,
        String actorNome, 
        String actorCognome, 
        Instant createdAt, 
        Instant readAt) {
    
}
