package com.bugboard26.modules.issue.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class CommentResponse {
    private String id;
    private String content;
    private String authorEmail;
    private Instant createdAt;
}
