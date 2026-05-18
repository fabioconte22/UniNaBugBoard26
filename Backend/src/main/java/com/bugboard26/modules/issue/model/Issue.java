package com.bugboard26.modules.issue.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "issues")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private String creatoreEmail;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;



    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUptade() {
        updatedAt = LocalDateTime.now();
    }


    
}
