package com.bugboard26.shared.messaging;

import com.bugboard26.common.messaging.IssueResolvedMessage;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration 
public class RabbitConfig {
    
    public static final String ISSUE_RESOLVED_QUEUE = "notification.issue-resolved"; 

    @Bean 
    public TopicExchange issuesExchange() {
        return new  TopicExchange(IssueResolvedMessage.EXCHANGE); 
    }

     @Bean
    public Queue issueResolvedQueue() {
        return new Queue(ISSUE_RESOLVED_QUEUE, true);
    }

    @Bean
    public Binding issueResolvedBinding(Queue issueResolvedQueue, TopicExchange issuesExchange) {
        return BindingBuilder.bind(issueResolvedQueue).to(issuesExchange).with(IssueResolvedMessage.ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
