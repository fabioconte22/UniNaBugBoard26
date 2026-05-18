package com.bugboard26.modules.issue.repository;

import com.bugboard26.modules.issue.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, String> {
    List<Issue> findByCreatorEmail(String creatorEmail);

    
}
