package com.bugboard26.modules.issue.dto;

import com.bugboard26.modules.issue.model.CommentKind;
import com.bugboard26.modules.issue.model.IssueStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class CommentResponse {
    private String id;
    private String content;
    private String authorEmail;
    private String authorNome;
    private String authorCognome; 
    private Instant createdAt;
    private CommentKind kind;
    private IssueStatus fromStatus;
    private IssueStatus toStatus;
}
