package com.bugboard26.modules.issue.repository;

import com.bugboard26.modules.issue.model.IssueStatus;
import com.bugboard26.modules.issue.model.IssueType;

public record TypeStatusCount(IssueType type, IssueStatus status, Long total ) {  
}

