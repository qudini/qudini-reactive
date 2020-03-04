package com.qudini.reactive.sqs.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qudini.reactive.sqs.listener.SqsListener;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

@Slf4j
public final class DefaultSqsMessageChecker implements SqsMessageChecker {

    private final SqsAsyncClient sqsClient;

    private final ObjectMapper objectMapper;

    public DefaultSqsMessageChecker(SqsAsyncClient sqsClient, ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> checkForMessages(String queueUrl, SqsListener<?> listener) {
        return fetchMessages(queueUrl)
                .flatMap(message -> handleMessage(queueUrl, message, listener))
                .then();
    }

    private Flux<Message> fetchMessages(String queueUrl) {
        return Mono
                .fromFuture(() -> {
                    var receiveMessageRequest = ReceiveMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .maxNumberOfMessages(10)
                            .visibilityTimeout(5)
                            .waitTimeSeconds(20)
                            .build();
                    return sqsClient.receiveMessage(receiveMessageRequest);
                })
                .filter(ReceiveMessageResponse::hasMessages)
                .flatMapIterable(ReceiveMessageResponse::messages);
    }

    @SneakyThrows
    private <T> Mono<Void> handleMessage(String queueUrl, Message message, SqsListener<T> listener) {
        var messageBody = objectMapper.readValue(message.body(), listener.getMessageType());
        var acknowledger = buildAcknowledger(queueUrl, message);
        return listener.handleMessage(messageBody, acknowledger);
    }

    private Acknowledger buildAcknowledger(String queueUrl, Message message) {
        return () -> Mono
                .fromFuture(() -> {
                    var deleteMessageRequest = DeleteMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .receiptHandle(message.receiptHandle())
                            .build();
                    return sqsClient.deleteMessage(deleteMessageRequest);
                })
                .then();
    }

}
