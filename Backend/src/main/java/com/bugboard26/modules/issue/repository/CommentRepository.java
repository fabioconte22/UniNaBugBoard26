package com.bugboard26.modules.issue.repository;

import com.bugboard26.modules.issue.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, String> {
    List<Comment> findByIssueIdOrderByCreatedAtAsc(String issueId);
}
    

