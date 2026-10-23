package com.bugboard26.shared.messaging;

import com.bugboard26.common.messaging.IssueResolvedMessage;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration 
public class RabbitConfig {

    @Bean 
    public TopicExchange issuesExchange() {
        return new TopicExchange(IssueResolvedMessage.EXCHANGE); 
    }

    @Bean 
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
}
