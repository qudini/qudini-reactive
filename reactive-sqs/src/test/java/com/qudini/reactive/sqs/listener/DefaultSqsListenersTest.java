package com.qudini.reactive.sqs.listener;

import com.qudini.reactive.logging.ReactiveLoggingContextCreator;
import com.qudini.reactive.sqs.message.Acknowledger;
import com.qudini.reactive.sqs.message.SqsMessageChecker;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.runAsync;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultSqsListeners")
class DefaultSqsListenersTest {

    private final SqsListener<String> listener = new SqsListener<>() {

        @Override
        public String getQueueName() {
            return "the-queue-name";
        }

        @Override
        public Class<String> getMessageType() {
            throw new IllegalStateException("shouldn't have been called");
        }

        @Override
        public Mono<Void> handleMessage(String message, Acknowledger acknowledger) {
            throw new IllegalStateException("shouldn't have been called");
        }

    };

    @Mock
    private SqsAsyncClient sqsClient;

    @Mock
    private SqsMessageChecker sqsMessageChecker;

    @Mock
    private ReactiveLoggingContextCreator reactiveLoggingContextCreator;

    @Test
    @DisplayName("should not start the long polling if the queue URL cannot be found")
    void queueNotFound() {
        var getQueueUrlRequest = GetQueueUrlRequest.builder()
                .queueName("the-queue-name")
                .build();
        var getQueueUrlResponse = CompletableFuture.<GetQueueUrlResponse>failedFuture(new IllegalStateException("fake queue url error"));
        given(sqsClient.getQueueUrl(getQueueUrlRequest)).willReturn(getQueueUrlResponse);
        startAndStop();
        verify(sqsMessageChecker, never()).checkForMessages(any(String.class), any(SqsListener.class));
    }

    @Test
    @DisplayName("should start the long polling")
    void polling() {

        var getQueueUrlRequest = GetQueueUrlRequest.builder()
                .queueName("the-queue-name")
                .build();
        var getQueueUrlResponse = GetQueueUrlResponse.builder()
                .queueUrl("the-queue-url")
                .build();
        given(sqsClient.getQueueUrl(getQueueUrlRequest)).willReturn(completedFuture(getQueueUrlResponse));

        var callCount = new AtomicInteger(0);
        var fakeMessageChecking = waitThenIncrement(callCount);
        given(sqsMessageChecker.checkForMessages("the-queue-url", listener)).willReturn(fakeMessageChecking);
        given(reactiveLoggingContextCreator.create(any(), any())).willReturn(Context.empty());

        startAndStop();

        verify(reactiveLoggingContextCreator, atLeast(2)).create(any(), any());
        assertThat(callCount.get()).isGreaterThanOrEqualTo(2);

    }

    @Test
    @DisplayName("should keep the long polling up if an error occurs while handling a message")
    void messageHandlerError() {

        var getQueueUrlRequest = GetQueueUrlRequest.builder()
                .queueName("the-queue-name")
                .build();
        var getQueueUrlResponse = GetQueueUrlResponse.builder()
                .queueUrl("the-queue-url")
                .build();
        given(sqsClient.getQueueUrl(getQueueUrlRequest)).willReturn(completedFuture(getQueueUrlResponse));

        var callCount = new AtomicInteger(0);
        var fakeMessageChecking = waitThenIncrement(callCount).then(Mono.<Void>error(new IllegalStateException("fake message checking error")));
        given(sqsMessageChecker.checkForMessages("the-queue-url", listener)).willReturn(fakeMessageChecking);
        given(reactiveLoggingContextCreator.create(any(), any())).willReturn(Context.empty());

        startAndStop();

        verify(reactiveLoggingContextCreator, atLeast(2)).create(any(), any());
        assertThat(callCount.get()).isGreaterThanOrEqualTo(2);

    }

    @SneakyThrows
    private void startAndStop() {
        var sqsListeners = new DefaultSqsListeners(Set.of(listener), sqsClient, sqsMessageChecker, reactiveLoggingContextCreator);
        runAsync(sqsListeners::start);
        Thread.sleep(100);
        sqsListeners.stop();
    }

    private static Mono<Void> waitThenIncrement(AtomicInteger counter) {
        var waitTime = Duration.of(10, MILLIS);
        return Mono.delay(waitTime).then(Mono.fromRunnable(counter::incrementAndGet));
    }

}
