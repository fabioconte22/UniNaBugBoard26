package com.bugboard26.modules.issue.dto;

import com.bugboard26.modules.issue.model.IssuePriority;
import com.bugboard26.modules.issue.model.IssueStatus;
import com.bugboard26.modules.issue.model.IssueType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class IssueResponse {
    private String id;
    private String titolo;
    private String descrizione;
    private IssueType type;
    private IssueStatus status;
    private IssuePriority prioroty;
    private String creatorEmail;
    private LocalDateTime createAt;
}
