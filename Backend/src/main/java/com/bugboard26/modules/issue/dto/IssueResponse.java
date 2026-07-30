package com.bugboard26.modules.issue.dto;

import com.bugboard26.modules.issue.model.IssuePriority;
import com.bugboard26.modules.issue.model.IssueStatus;
import com.bugboard26.modules.issue.model.IssueType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class IssueResponse {
    private String id;
    private String titolo;
    private String descrizione;
    private String imageUrl; 
    private IssueType type;
    private IssueStatus status;
    private IssuePriority priority;
    private String creatorEmail;
    private String assigneeEmail; 
    private Instant createdAt;
}
