package com.bugboard26.modules.issue.service;

import com.bugboard26.modules.auth.service.AuthService;
import com.bugboard26.modules.issue.dto.IssueResponse;
import com.bugboard26.modules.issue.model.Issue;
import com.bugboard26.modules.issue.model.IssuePriority;
import com.bugboard26.modules.issue.model.IssueStatus;
import com.bugboard26.modules.issue.model.IssueType;
import com.bugboard26.modules.issue.repository.CommentRepository;
import com.bugboard26.modules.issue.repository.IssueRepository;
import com.bugboard26.modules.notification.service.NotificationService;
import com.bugboard26.shared.exception.InvalidFilterParameterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

// Black-Box: IssueService.getFilteredIssues con classi di equivalenza e Pair-Wise.
@DataJpaTest 
class IssueServiceFilterTest {

    private static final Pageable ALL = PageRequest.of(0,100); 

    @Autowired 
    private IssueRepository issueRepository;

    @Autowired 
    private CommentRepository commentRepository;

    private IssueService issueService; 

    @BeforeEach 
    void setUp() {
        issueService = new IssueService(
            issueRepository, 
            mock(FileStorageService.class),
            mock(AuthService.class),
            commentRepository, 
            mock(NotificationService.class)); 
        
        List<Issue> issues = new ArrayList<>(); 
        for (IssueStatus status : IssueStatus.values()) {
            for (IssueType type : IssueType.values()) {
                for (IssuePriority priority : IssuePriority.values()) {
                    issues.add(Issue.builder()
                            .titolo("Issue di test")
                            .descrizione("Descrizione")
                            .status(status)
                            .type(type)
                            .priority(priority)
                            .creatorEmail("test@bugboard26.com")
                            .build()); 
                }
            }
        }
        issueRepository.saveAll(issues); 
    }

    @ParameterizedTest(name = "Test {0}: status={1}, type={2}, priority={3}")
    @CsvSource({
            " 1, TODO,        QUESTION,      LOW",
            " 2, TODO,        BUG,           MEDIUM",
            " 3, TODO,        DOCUMENTATION, HIGH",
            " 4, TODO,        FEATURE,       CRITICAL",
            " 5, TODO,        ,              ",
            " 6, IN_PROGRESS, QUESTION,      MEDIUM",
            " 7, IN_PROGRESS, BUG,           HIGH",
            " 8, IN_PROGRESS, DOCUMENTATION, CRITICAL",
            " 9, IN_PROGRESS, FEATURE,       ",
            "10, IN_PROGRESS, ,              LOW",
            "11, RESOLVED,    QUESTION,      HIGH",
            "12, RESOLVED,    BUG,           CRITICAL",
            "13, RESOLVED,    DOCUMENTATION, ",
            "14, RESOLVED,    FEATURE,       LOW",
            "15, RESOLVED,    ,              MEDIUM",
            "16, CLOSED,      QUESTION,      CRITICAL",
            "17, CLOSED,      BUG,           ",
            "18, CLOSED,      DOCUMENTATION, LOW",
            "19, CLOSED,      FEATURE,       MEDIUM",
            "20, CLOSED,      ,              HIGH",
            "21, ,            QUESTION,      ",
            "22, ,            BUG,           LOW",
            "23, ,            DOCUMENTATION, MEDIUM",
            "24, ,            FEATURE,       HIGH",
            "25, ,            ,              CRITICAL",
    })
    void getFilteredIssues_validClassesPairWise(int testId, String status, String type, String priority) {
        List<IssueResponse> result = issueService.getFilteredIssues(status, type, priority, ALL).getContent(); 

        assertThat(result)
                .hasSize(expectedSize(status, type, priority))
                .allMatch(issue -> status == null || issue.getStatus() == IssueStatus.valueOf(status))
                .allMatch(issue -> type == null || issue.getType() == IssueType.valueOf(type))
                .allMatch(issue -> priority == null || issue.getPriority() == IssuePriority.valueOf(priority)); 
    }

    // La classe "nessun filtro" comprende due casi: null(parametro assente) e stringa vuota o fatta di spazi(parametro presente ma vuoto)
    @Test
    void getFilteredIssues_blankFilters_returnAllIssues() {
        List<IssueResponse> result = issueService.getFilteredIssues("", " ", "   ", ALL).getContent(); 

        assertThat(result).hasSize(64);  
    }

    // Classi non valide

    @Test
    void getFilteredIssues_invalidStatus_throwsException() {
        assertThatThrownBy(() -> issueService.getFilteredIssues("APERTA", "BUG", "LOW", ALL))
                    .isInstanceOf(InvalidFilterParameterException.class); 
    }

    @Test
    void getFilteredIssues_invalidType_throwsException() {
        assertThatThrownBy(() -> issueService.getFilteredIssues("TODO", "ERRORE", "LOW", ALL))
                    .isInstanceOf(InvalidFilterParameterException.class); 
    }

    @Test
    void getFilteredIssues_invalidPriority_throwsException() {
        assertThatThrownBy(() -> issueService.getFilteredIssues("TODO", "BUG", "URGENTE", ALL))
                    .isInstanceOf(InvalidFilterParameterException.class); 
    }

    private static int expectedSize(String status, String type, String priority) {
        int size = 1;
        if (status == null) size *= IssueStatus.values().length;
        if (type == null) size *= IssueType.values().length;
        if (priority == null) size *= IssuePriority.values().length;
        return size;
    }


    
}
