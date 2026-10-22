package com.bugboard26.modules.issue.service;

import com.bugboard26.modules.auth.service.AuthService;
import com.bugboard26.modules.issue.dto.IssueResponse;
import com.bugboard26.modules.issue.model.Comment;
import com.bugboard26.modules.issue.model.CommentKind;
import com.bugboard26.modules.issue.model.Issue;
import com.bugboard26.modules.issue.model.IssuePriority;
import com.bugboard26.modules.issue.model.IssueStatus;
import com.bugboard26.modules.issue.model.IssueType;
import com.bugboard26.modules.issue.repository.CommentRepository;
import com.bugboard26.modules.issue.repository.IssueRepository;
import com.bugboard26.modules.notification.service.NotificationService;
import com.bugboard26.shared.exception.InvalidFilterParameterException;
import com.bugboard26.shared.exception.InvalidStatusTransitionException;
import com.bugboard26.shared.exception.IssueAccessDeniedException;
import com.bugboard26.shared.exception.IssueNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

//White-Box: IssueService.updateStatus
@ExtendWith(MockitoExtension.class)
class IssueServiceUpdateStatusTest {
    
    private static final String ISSUE_ID = "issue-1";
    private static final String REPORTER = "reporter@bugboard26.com";
    private static final String ASSIGNEE = "assignee@bugboard26.com";
    private static final String OTHER = "other@bugboard26.com";
    private static final String ADMIN = "admin@bugboard26.com";

    @Mock
    private IssueRepository issueRepository;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private AuthService authService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private IssueService issueService; 

    @Test 
    void updateStatus_issueNotFound_throwsNotFound() {
        when(issueRepository.findById(ISSUE_ID)).thenReturn(Optional.empty()); 

        assertThatThrownBy(() -> issueService.updateStatus(ISSUE_ID, "IN_PROGRESS", null, ASSIGNEE, false))
                    .isInstanceOf(IssueNotFoundException.class); 
        assertNothingSaved(); 
    }

    @Test
    void updateStatus_unknownTargetStatus_throwsInvalidParameter() {
        givenIssue(IssueType.BUG, IssueStatus.TODO, ASSIGNEE); 

        assertThatThrownBy(() -> issueService.updateStatus(ISSUE_ID, "ARCHIVIATA", null, ASSIGNEE, false))
                    .isInstanceOf(InvalidFilterParameterException.class); 
        assertNothingSaved(); 
    }

    @Test 
    void updateStatus_bugStartedByNonAssignee_throwsAccessDenied() {
        givenIssue(IssueType.BUG, IssueStatus.TODO, ASSIGNEE); 

        assertThatThrownBy(() -> issueService.updateStatus(ISSUE_ID, "IN_PROGRESS", null, OTHER, false))
                    .isInstanceOf(IssueAccessDeniedException.class); 
        assertNothingSaved(); 
    }

    @Test 
    void updateStatus_resolvedByNonAssignee_throwsAccessDenied() {
        givenIssue(IssueType.BUG, IssueStatus.IN_PROGRESS, ASSIGNEE); 

        assertThatThrownBy(() -> issueService.updateStatus(ISSUE_ID, "RESOLVED", "Fatto", OTHER, false))
                    .isInstanceOf(IssueAccessDeniedException.class); 
        assertNothingSaved(); 
    }

    @Test 
    void updateStatus_resolvedWithoutReport_throwsInvalidTransition() {
        givenIssue(IssueType.BUG, IssueStatus.IN_PROGRESS, ASSIGNEE); 

        assertThatThrownBy(() -> issueService.updateStatus(ISSUE_ID, "RESOLVED", "  ", ASSIGNEE, false))
                    .isInstanceOf(InvalidStatusTransitionException.class); 
        assertNothingSaved();
    }

    @Test 
    void updateStatus_closedByNonAdmin_throwsAccessDenied() {
        givenIssue(IssueType.BUG, IssueStatus.RESOLVED, ASSIGNEE); 

        assertThatThrownBy(() -> issueService.updateStatus(ISSUE_ID, "CLOSED", null, ASSIGNEE, false)) 
                    .isInstanceOf(IssueAccessDeniedException.class); 
        assertNothingSaved();            
    }

    @Test 
    void updateStatus_transitionNotAllowed_throwsInvalidTransition() {
        givenIssue(IssueType.BUG, IssueStatus.CLOSED, ASSIGNEE); 

        assertThatThrownBy(() -> issueService.updateStatus(ISSUE_ID, "TODO", null, ADMIN, true)) 
                    .isInstanceOf(InvalidStatusTransitionException.class); 
        assertNothingSaved();
    }

    @Test
    void updateStatus_todoToResolved_throwsInvalidTransition() {
        givenIssue(IssueType.BUG, IssueStatus.TODO, ASSIGNEE); 

        assertThatThrownBy(() -> issueService.updateStatus(ISSUE_ID, "RESOLVED", "Fatto", ASSIGNEE, false)) 
                    .isInstanceOf(InvalidStatusTransitionException.class); 
        assertNothingSaved();
    }

    @Test
    void updateStatus_inProgressToClosed_throwsInvalidTransition()  {
        givenIssue(IssueType.BUG, IssueStatus.IN_PROGRESS, ASSIGNEE); 

        assertThatThrownBy(() -> issueService.updateStatus(ISSUE_ID, "CLOSED", null, ADMIN, true))
                    .isInstanceOf(InvalidStatusTransitionException.class); 
        assertNothingSaved();
    }

    @Test
    void updateStatus_resolvedToTodo_throwsInvalidTransition()  {
        givenIssue(IssueType.BUG, IssueStatus.RESOLVED, ASSIGNEE); 

        assertThatThrownBy(() -> issueService.updateStatus(ISSUE_ID, "TODO", null, ADMIN, true)) 
                    .isInstanceOf(InvalidStatusTransitionException.class); 
        assertNothingSaved();
    }

    // --- Cammini che arrivano al salvataggio 

    @Test 
    void updateStatus_nonBugWithoutAssigneeStarted_callerTakesCharge() {
        Issue issue = givenIssue(IssueType.QUESTION, IssueStatus.TODO, null); 
        givenSaveReturnsArgument(); 

        IssueResponse response = issueService.updateStatus(ISSUE_ID, "IN_PROGRESS", null, OTHER, false); 

        assertThat(response.getStatus()).isEqualTo(IssueStatus.IN_PROGRESS); 
        assertThat(issue.getAssigneeEmail()).isEqualTo(OTHER); 
        assertHistoryEntry(IssueStatus.TODO, IssueStatus.IN_PROGRESS, OTHER, ""); 
        verifyNoInteractions(notificationService); 
    }

    @Test 
    void updateStatus_nonBugAlreadyAssignedStartedByAssignee_isAllowed() {
        Issue issue = givenIssue(IssueType.FEATURE, IssueStatus.TODO, ASSIGNEE);
        givenSaveReturnsArgument();  

        IssueResponse response = issueService.updateStatus(ISSUE_ID, "IN_PROGRESS", null, ASSIGNEE, false); 

        assertThat(response.getStatus()).isEqualTo(IssueStatus.IN_PROGRESS);
        assertThat(issue.getAssigneeEmail()).isEqualTo(ASSIGNEE);
        assertHistoryEntry(IssueStatus.TODO, IssueStatus.IN_PROGRESS, ASSIGNEE, "");
        verifyNoInteractions(notificationService);
    }

    @Test 
    void updateStatus_bugStartedByAssignee_isAllowed() {
        Issue issue = givenIssue(IssueType.BUG, IssueStatus.TODO, ASSIGNEE); 
        givenSaveReturnsArgument();  

        IssueResponse response = issueService.updateStatus(ISSUE_ID, "IN_PROGRESS", null, ASSIGNEE, false); 

        assertThat(response.getStatus()).isEqualTo(IssueStatus.IN_PROGRESS);
        assertThat(issue.getAssigneeEmail()).isEqualTo(ASSIGNEE);
        assertHistoryEntry(IssueStatus.TODO, IssueStatus.IN_PROGRESS, ASSIGNEE, "");
        verifyNoInteractions(notificationService);
    }

    @Test
    void updateStatus_resolvedByAssigneeWithReport_notifiesReporter() {
        givenIssue(IssueType.BUG, IssueStatus.IN_PROGRESS, ASSIGNEE);
        givenSaveReturnsArgument();

        IssueResponse response = issueService.updateStatus(ISSUE_ID, "RESOLVED", "  Corretto il login  ", ASSIGNEE, false);

        assertThat(response.getStatus()).isEqualTo(IssueStatus.RESOLVED);
        assertHistoryEntry(IssueStatus.IN_PROGRESS, IssueStatus.RESOLVED, ASSIGNEE, "Corretto il login");
    
        verify(notificationService).notifyIssueResolved(ISSUE_ID, "Bug di prova", REPORTER, ASSIGNEE);
    }

    @Test
    void updateStatus_closedByAdmin_isAllowedWithoutNotification() {
        givenIssue(IssueType.BUG, IssueStatus.RESOLVED, ASSIGNEE);
        givenSaveReturnsArgument();

        IssueResponse response = issueService.updateStatus(ISSUE_ID, "CLOSED", null, ADMIN, true);

        assertThat(response.getStatus()).isEqualTo(IssueStatus.CLOSED);
        assertHistoryEntry(IssueStatus.RESOLVED, IssueStatus.CLOSED, ADMIN, "");
        verifyNoInteractions(notificationService);
    } 

    @Test
    void updateStatus_reopenedByAdmin_isAllowedWithoutNotification() {
        Issue issue = givenIssue(IssueType.BUG, IssueStatus.RESOLVED, ASSIGNEE);
        givenSaveReturnsArgument();

        IssueResponse response = issueService.updateStatus(ISSUE_ID, "IN_PROGRESS", "Il bug si ripresenta", ADMIN, true);

        assertThat(response.getStatus()).isEqualTo(IssueStatus.IN_PROGRESS);
        
        assertThat(issue.getAssigneeEmail()).isEqualTo(ASSIGNEE);
        assertHistoryEntry(IssueStatus.RESOLVED, IssueStatus.IN_PROGRESS, ADMIN, "Il bug si ripresenta");
        verifyNoInteractions(notificationService);
    }

    // --- Metodi di supporto 

    private Issue givenIssue(IssueType type, IssueStatus status, String assignee) {
        Issue issue = Issue.builder()
                .id(ISSUE_ID)
                .titolo("Bug di prova")
                .descrizione("Descrizione")
                .type(type)
                .status(status)
                .priority(IssuePriority.HIGH)
                .creatorEmail(REPORTER)
                .assigneeEmail(assignee)
                .build();
        when(issueRepository.findById(ISSUE_ID)).thenReturn(Optional.of(issue));
        return issue;
    }

    private void givenSaveReturnsArgument() {
        when(issueRepository.save(any(Issue.class))).thenAnswer(returnsFirstArg());
    }

    private void assertHistoryEntry(IssueStatus from, IssueStatus to, String author, String content) {
        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());
        Comment entry = captor.getValue();
        assertThat(entry.getKind()).isEqualTo(CommentKind.STATUS_CHANGE);
        assertThat(entry.getFromStatus()).isEqualTo(from);
        assertThat(entry.getToStatus()).isEqualTo(to);
        assertThat(entry.getAuthorEmail()).isEqualTo(author);
        assertThat(entry.getContent()).isEqualTo(content);
    }

    private void assertNothingSaved() {
        verify(issueRepository, never()).save(any());
        verify(commentRepository, never()).save(any());
        verify(notificationService, never()).notifyIssueResolved(anyString(), anyString(), anyString(), anyString());
    }



}
