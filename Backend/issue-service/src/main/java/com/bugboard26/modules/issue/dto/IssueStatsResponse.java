package com.bugboard26.modules.issue.dto;

import com.bugboard26.modules.issue.model.IssuePriority;
import com.bugboard26.modules.issue.model.IssueStatus;
import com.bugboard26.modules.issue.model.IssueType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record IssueStatsResponse (
        Map<IssueStatus, Long> byStatus,
        Map<IssueType, Map<IssueStatus, Long>> byTypeAndStatus,
        Map<IssuePriority, Long> openByPriority, 
        List<WeekTrend> lastWeeks) {
    
    public record WeekTrend(LocalDate weekStart, long created, long closed) {
    }
}
