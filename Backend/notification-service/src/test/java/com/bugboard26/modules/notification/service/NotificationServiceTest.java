package com.bugboard26.modules.notification.service;

import com.bugboard26.common.messaging.IssueResolvedMessage;
import com.bugboard26.modules.notification.model.Notification;
import com.bugboard26.modules.notification.model.NotificationKind;
import com.bugboard26.modules.notification.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    private static final String REPORTER = "segnalatore@bugboard26.com";
    private static final String RESOLVER = "risolutore@bugboard26.com";

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void notifyIssueResolved_byAnotherUser_savesNotificationForReporter() {
        notificationService.notifyIssueResolved(
                new IssueResolvedMessage("issue-1", "Bug di prova", REPORTER, RESOLVER, "Mario", "Rossi"));

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        Notification saved = captor.getValue();
        assertThat(saved.getRecipientEmail()).isEqualTo(REPORTER);
        assertThat(saved.getKind()).isEqualTo(NotificationKind.ISSUE_RESOLVED);
        assertThat(saved.getIssueId()).isEqualTo("issue-1");
        assertThat(saved.getIssueTitolo()).isEqualTo("Bug di prova");
        assertThat(saved.getActorEmail()).isEqualTo(RESOLVER);
        assertThat(saved.getActorNome()).isEqualTo("Mario");
        assertThat(saved.getActorCognome()).isEqualTo("Rossi");
    }

    @Test
    void notifyIssueResolved_byReporterThemself_savesNothing() {
        notificationService.notifyIssueResolved(
                new IssueResolvedMessage("issue-1", "Bug di prova", REPORTER, REPORTER, "Mario", "Rossi"));

        verifyNoInteractions(notificationRepository);
    }
}
