package com.qudini.reactive.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qudini.reactive.logging.ReactiveLoggingContextCreator;
import com.qudini.reactive.sqs.listener.DefaultSqsListeners;
import com.qudini.reactive.sqs.listener.SqsListener;
import com.qudini.reactive.sqs.listener.SqsListeners;
import com.qudini.reactive.sqs.message.DefaultSqsMessageChecker;
import com.qudini.reactive.sqs.message.SqsMessageChecker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.util.Collection;

@Configuration
@Slf4j
public class ReactiveSqsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SqsAsyncClient sqsClient() {
        return SqsAsyncClient.create();
    }

    @Bean
    @ConditionalOnMissingBean
    public SqsMessageChecker sqsMessageChecker(SqsAsyncClient sqsClient, ObjectMapper objectMapper) {
        return new DefaultSqsMessageChecker(sqsClient, objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public SqsListeners sqsListeners(Collection<SqsListener<?>> listeners, SqsAsyncClient sqsClient, SqsMessageChecker sqsMessageChecker, ReactiveLoggingContextCreator reactiveLoggingContextCreator) {
        return new DefaultSqsListeners(listeners, sqsClient, sqsMessageChecker, reactiveLoggingContextCreator);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start(ApplicationReadyEvent applicationReadyEvent) {
        applicationReadyEvent
                .getApplicationContext()
                .getBean("sqsListeners", SqsListeners.class)
                .start();
    }

}
