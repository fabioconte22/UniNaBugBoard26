package com.bugboard26.modules.issue.repository;

import com.bugboard26.modules.issue.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, String>, JpaSpecificationExecutor<Issue> {
    List<Issue> findByCreatorEmail(String creatorEmail);
    List<Issue> findByAssigneeEmail(String assigneeEmail);
    
}
