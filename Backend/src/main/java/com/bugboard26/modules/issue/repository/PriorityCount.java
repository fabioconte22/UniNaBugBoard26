package com.bugboard26.modules.issue.repository;

import com.bugboard26.modules.issue.model.IssuePriority;

public record PriorityCount(IssuePriority priority, Long total) {
}
