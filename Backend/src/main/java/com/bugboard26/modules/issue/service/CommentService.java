package com.bugboard26.modules.issue.service;

import com.bugboard26.modules.issue.dto.CommentResponse;
import com.bugboard26.modules.issue.dto.CreateCommentRequest;
import com.bugboard26.modules.issue.model.Comment;
import com.bugboard26.modules.issue.model.Issue;
import com.bugboard26.modules.issue.repository.CommentRepository;
import com.bugboard26.modules.issue.repository.IssueRepository;
import com.bugboard26.shared.exception.IssueNotFoundException;
import com.bugboard26.modules.auth.dto.UserResponse;
import com.bugboard26.modules.auth.service.AuthService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final IssueRepository issueRepository;
    private final AuthService authService; 

    public CommentResponse addComment(String issueId, CreateCommentRequest request, String authorEmail) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IssueNotFoundException(issueId));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .authorEmail(authorEmail)
                .issue(issue)
                .build();

        Comment saved = commentRepository.save(comment);
        return toResponse(saved, authService.findUsersByEmails(List.of(authorEmail)));
    }

    public List<CommentResponse> getComments(String issueId) {
        if (!issueRepository.existsById(issueId)) {
            throw new IssueNotFoundException(issueId);
        }
        List<Comment> comments = commentRepository.findByIssueIdOrderByCreatedAtAsc(issueId);

        Map<String, UserResponse> authors = authService.findUsersByEmails(
            comments.stream().map(Comment::getAuthorEmail).collect(Collectors.toSet()));
        
        return comments.stream()
                .map(comment -> toResponse(comment, authors))
                .collect(Collectors.toList());
    }

    private CommentResponse toResponse(Comment comment, Map<String, UserResponse> authors) {
        UserResponse author = authors.get(comment.getAuthorEmail());
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getAuthorEmail(),
                author != null ? author.nome() : null,
                author != null ? author.cognome() : null,
                comment.getCreatedAt()
        );
    }
}
