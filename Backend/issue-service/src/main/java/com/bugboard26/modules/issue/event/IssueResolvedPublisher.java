package com.bugboard26.modules.issue.event;

import com.bugboard26.common.messaging.IssueResolvedMessage;
import com.bugboard26.shared.user.UserDirectory;
import com.bugboard26.shared.user.UserSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class IssueResolvedPublisher {
    
    private final RabbitTemplate rabbitTemplate; 
    private final UserDirectory userDirectory; 

    @TransactionalEventListener 
    public void onIssueResolved(IssueResolvedEvent event) {
        UserSummary resolver = userDirectory.findByEmails(List.of(event.resolverEmail())).get(event.resolverEmail()); 
        IssueResolvedMessage message = new IssueResolvedMessage(
                event.issueId(), 
                event.issueTitolo(),
                event.reporterEmail(),
                event.resolverEmail(), 
                resolver != null ? resolver.nome() : null,
                resolver != null ? resolver.cognome() : null);
        try {
            rabbitTemplate.convertAndSend(IssueResolvedMessage.EXCHANGE, IssueResolvedMessage.ROUTING_KEY, message);
        } catch (AmqpException ex) {
            log.warn("Notifica della issue {} non inviata: RabbitMQ non raggiungibile ({})", event.issueId(), ex.getMessage()); 
        }
    }

}
