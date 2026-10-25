package com.bugboard26.modules.issue.repository;

import com.bugboard26.modules.issue.model.IssueStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.bugboard26.modules.issue.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, String>, JpaSpecificationExecutor<Issue> {
    List<Issue> findByCreatorEmail(String creatorEmail);
    List<Issue> findByAssigneeEmail(String assigneeEmail);

    @Query("select new com.bugboard26.modules.issue.repository.TypeStatusCount(i.type, i.status, count(i)) "
            + "from Issue i group by i.type, i.status")
    List<TypeStatusCount> countByTypeAndStatus();

    @Query("select new com.bugboard26.modules.issue.repository.PriorityCount(i.priority, count(i)) "
            + "from Issue i where i.status in :statuses group by i.priority")
    List<PriorityCount> countByPriorityForStatuses(@Param("statuses") Collection<IssueStatus> statuses);

    @Query("select i.createdAt from Issue i where i.createdAt >= :since")
    List<Instant> findCreationTimesSince(@Param("since") Instant since);
}
