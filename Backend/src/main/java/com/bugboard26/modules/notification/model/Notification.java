package com.bugboard26.modules.notification.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Notification {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id; 

    @Column(nullable = false)
    private String recipientEmail; 

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationKind kind;

    @Column(nullable = false)
    private String issueId; 

    @Column(nullable = false)
    private String issueTitolo; 

    @Column(nullable = false)
    private String actorEmail; 

    @CreatedDate 
    @Column(nullable = false, updatable = false)
    private Instant createdAt; 

    private Instant readAt; 

}