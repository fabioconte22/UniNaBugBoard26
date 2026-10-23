package com.bugboard26.modules.issue.event;

public record IssueResolvedEvent(String issueId, String issueTitolo, String reporterEmail, String resolverEmail) {
}
