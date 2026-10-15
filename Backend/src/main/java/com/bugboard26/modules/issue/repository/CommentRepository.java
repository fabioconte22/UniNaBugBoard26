package com.bugboard26.modules.issue.repository;

import com.bugboard26.modules.issue.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import com.bugboard26.modules.issue.model.CommentKind;
import com.bugboard26.modules.issue.model.IssueStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, String> {
    List<Comment> findByIssueIdOrderByCreatedAtAsc(String issueId);

    @Query("select c.createdAt from Comment c " + "where c.kind = :kind and c.toStatus = :toStatus and c.createdAt >= :since")
    List<Instant> findTransitionTimesSince(@Param("kind") CommentKind kind,
                                           @Param("toStatus") IssueStatus toStatus,
                                           @Param("since") Instant since);
}
    

