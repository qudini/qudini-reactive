package com.qudini.reactive.sqs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qudini.reactive.logging.ReactiveLoggingContextCreator;
import com.qudini.reactive.sqs.listener.SqsListeners;
import com.qudini.reactive.sqs.message.DefaultSqsMessageChecker;
import com.qudini.reactive.sqs.message.SqsMessageChecker;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.util.Collection;

@AutoConfiguration
public class ReactiveSqsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SqsAsyncClient sqsClient() {
        return SqsAsyncClient.create();
    }

    @Bean
    @ConditionalOnMissingBean(name = "sqsMessageObjectMapper")
    public ObjectMapper sqsMessageObjectMapper(ObjectMapper objectMapper) {
        return objectMapper;
    }

    @Bean
    @ConditionalOnMissingBean
    public SqsMessageChecker sqsMessageChecker(SqsAsyncClient sqsClient, ObjectMapper sqsMessageObjectMapper) {
        return new DefaultSqsMessageChecker(sqsClient, sqsMessageObjectMapper);
    }

    @Bean
    public SqsListeners sqsListeners(Collection<SqsListener<?>> listeners, SqsAsyncClient sqsClient, SqsMessageChecker sqsMessageChecker, ReactiveLoggingContextCreator reactiveLoggingContextCreator) {
        return new SqsListeners(listeners, sqsClient, sqsMessageChecker, reactiveLoggingContextCreator);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void start(ApplicationReadyEvent applicationReadyEvent) {
        applicationReadyEvent
                .getApplicationContext()
                .getBean(SqsListeners.class)
                .start();
    }

}
