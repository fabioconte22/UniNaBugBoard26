package com.bugboard26.modules.notification.messaging;

import com.bugboard26.common.messaging.IssueResolvedMessage;
import com.bugboard26.modules.notification.service.NotificationService;
import com.bugboard26.shared.messaging.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component 
@RequiredArgsConstructor 
public class IssueResolvedListener {
    
    private final NotificationService notificationService; 

    @RabbitListener(queues = RabbitConfig.ISSUE_RESOLVED_QUEUE)
    public void onIssueResolved(IssueResolvedMessage message) {
        notificationService.notifyIssueResolved(message);
    }
}
