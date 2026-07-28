package com.bugboard26.modules.issue.controller;

import com.bugboard26.modules.issue.dto.CommentResponse;
import com.bugboard26.modules.issue.dto.CreateCommentRequest;
import com.bugboard26.modules.issue.service.CommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issue/{issueId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable String issueId,
            @Valid @RequestBody CreateCommentRequest request,
            @AuthenticationPrincipal String email) {

        CommentResponse response = commentService.addComment(issueId, request, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable String issueId) {
        return ResponseEntity.ok(commentService.getComments(issueId));
    }
}
