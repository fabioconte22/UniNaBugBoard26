package com.bugboard26.modules.issue.service;

import com.bugboard26.modules.issue.dto.IssueResponse;
import com.bugboard26.modules.issue.model.Issue;
import com.bugboard26.modules.issue.model.IssuePriority;
import com.bugboard26.modules.issue.model.IssueStatus;
import com.bugboard26.modules.issue.model.IssueType;
import com.bugboard26.modules.issue.repository.CommentRepository;
import com.bugboard26.modules.issue.repository.IssueRepository;
import com.bugboard26.shared.exception.InvalidIssueTypeForAssignmentException;
import com.bugboard26.shared.exception.IssueNotFoundException;
import com.bugboard26.shared.exception.UserNotFoundException;
import com.bugboard26.shared.user.UserDirectory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

//Black-Box: IssueService.assignIssue
@ExtendWith(MockitoExtension.class)
class IssueServiceAssignTest {
    private static final String ISSUE_ID = "issue-1"; 
    private static final String MISSING_ISSUE_ID = "issue-inesistente"; 
    private static final String REPORTER = "reporter@bugboard26.com";
    private static final String MEMBER = "member@bugboard26.com";
    private static final String UNKNOWN = "unknown@bugboard26.com";

    @Mock 
    private IssueRepository issueRepository; 
    @Mock 
    private FileStorageService fileStorageService; 
    @Mock 
    private UserDirectory userDirectory; 
    @Mock 
    private CommentRepository commentRepository; 
    @Mock 
    private ApplicationEventPublisher eventPublisher; 

    @InjectMocks 
    private IssueService issueService; 

    // CE1 (bug esistente) + CE4 (utente esistente)
    @Test 
    void assignIssue_existingBugAndExistingUser_assignsBug() {
        givenIssue(IssueType.BUG); 
        when(issueRepository.save(any(Issue.class))).then(returnsFirstArg()); 

        IssueResponse response = issueService.assignIssue(ISSUE_ID, MEMBER); 

        assertThat(response.getAssigneeEmail()).isEqualTo(MEMBER); 
        verify(userDirectory).ensureUserExists(MEMBER);
        verify(issueRepository).save(any(Issue.class)); 
    }

    // CE2 (issue esistente ma non di tipo bug) + CE4
    @ParameterizedTest 
    @EnumSource(value = IssueType.class, names = {"QUESTION", "DOCUMENTATION", "FEATURE"})
    void assignIssue_existingIssueNotBug_throwsInvalidType(IssueType type) {
        givenIssue(type); 

        assertThatThrownBy(() -> issueService.assignIssue(ISSUE_ID, MEMBER))
                .isInstanceOf(InvalidIssueTypeForAssignmentException.class); 
        verifyNoInteractions(userDirectory); 
        verify(issueRepository, never()).save(any());
    }

    // CE3 (issue inesistente) + CE4
    @Test 
    void assignIssue_missingIssue_throwsNotFound() {
        when(issueRepository.findById(MISSING_ISSUE_ID)).thenReturn(Optional.empty()); 

        assertThatThrownBy(() -> issueService.assignIssue(MISSING_ISSUE_ID, MEMBER))
                .isInstanceOf(IssueNotFoundException.class); 
        verifyNoInteractions(userDirectory);
        verify(issueRepository, never()).save(any()); 
    }

    // CE1 + CE5 (utente inesistente)
    @Test
    void assignIssue_existingBugAndUnknownUser_throwsUserNotFound() {
        Issue issue = givenIssue(IssueType.BUG); 
        doThrow(new UserNotFoundException(UNKNOWN)).when(userDirectory).ensureUserExists(UNKNOWN);

        assertThatThrownBy(() -> issueService.assignIssue(ISSUE_ID, UNKNOWN))
                .isInstanceOf(UserNotFoundException.class);
        assertThat(issue.getAssigneeEmail()).isNull();
        verify(issueRepository, never()).save(any());
    }

    private Issue givenIssue(IssueType type) {
        Issue issue = Issue.builder()
                .id(ISSUE_ID)
                .titolo("Issue di prova")
                .descrizione("Descrizione")
                .type(type) 
                .status(IssueStatus.TODO)
                .priority(IssuePriority.MEDIUM)
                .creatorEmail(REPORTER)
                .build();
        when(issueRepository.findById(ISSUE_ID)).thenReturn(Optional.of(issue)); 
        return issue; 

    }

}
