package com.bugboard26.modules.issue.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "issues")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Issue {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String titolo;
    
    @Column(nullable = false, length = 2000)
    private String descrizione;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueType type;

    @Enumerated(EnumType.STRING) 
    @Column(nullable = false)
    private IssueStatus status;

    @Enumerated(EnumType.STRING) 
    private IssuePriority priority;

    @Column(nullable = false) 
    private String creatorEmail;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;
    
}
