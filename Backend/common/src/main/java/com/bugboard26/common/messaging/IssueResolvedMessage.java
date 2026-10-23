package com.bugboard26.common.messaging;

public record IssueResolvedMessage(
        String issueId,
        String issueTitolo, 
        String reporterEmail, 
        String resolverEmail, 
        String resolverNome, 
        String resolverCognome) {
    
    public static final String EXCHANGE = "bugboard.issues"; 
    public static final String ROUTING_KEY = "issue.resolved"; 
}
