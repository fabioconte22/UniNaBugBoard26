package com.bugboard26.modules.issue.dto;

import com.bugboard26.modules.issue.model.IssuePriority;
import com.bugboard26.modules.issue.model.IssueType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateIssueRequest {
    private String titolo;
    private String descrizione;
    private IssueType type;
    private IssuePriority priority;
}
