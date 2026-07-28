package com.bugboard26.modules.issue.service;

import com.bugboard26.modules.issue.dto.CommentResponse;
import com.bugboard26.modules.issue.dto.CreateCommentRequest;
import com.bugboard26.modules.issue.model.Comment;
import com.bugboard26.modules.issue.model.Issue;
import com.bugboard26.modules.issue.repository.CommentRepository;
import com.bugboard26.modules.issue.repository.IssueRepository;
import com.bugboard26.shared.exception.IssueNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final IssueRepository issueRepository;

    public CommentResponse addComment(String issueId, CreateCommentRequest request, String authorEmail) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IssueNotFoundException(issueId));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .authorEmail(authorEmail)
                .issue(issue)
                .build();

        Comment saved = commentRepository.save(comment);
        return toResponse(saved);
    }

    public List<CommentResponse> getComments(String issueId) {
        if (!issueRepository.existsById(issueId)) {
            throw new IssueNotFoundException(issueId);
        }

        return commentRepository.findByIssueIdOrderByCreatedAtAsc(issueId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private CommentResponse toResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getAuthorEmail(),
                comment.getCreatedAt()
        );
    }
}
