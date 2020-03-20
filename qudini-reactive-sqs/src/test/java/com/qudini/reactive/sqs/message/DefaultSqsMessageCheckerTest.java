package com.qudini.reactive.sqs.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qudini.reactive.sqs.SqsListener;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("DefaultSqsMessageChecker")
class DefaultSqsMessageCheckerTest {

    private final SqsListener<String> acknowledgingListener = new SqsListener<>() {

        @Override
        public String getQueueName() {
            return "the-queue-name";
        }

        @Override
        public Class<String> getMessageType() {
            return String.class;
        }

        @Override
        public Mono<Void> handleMessage(String message, Acknowledger acknowledger) {
            return acknowledger.acknowledge();
        }

    };

    private final SqsListener<String> failingListener = new SqsListener<>() {

        @Override
        public String getQueueName() {
            return "the-queue-name";
        }

        @Override
        public Class<String> getMessageType() {
            return String.class;
        }

        @Override
        public Mono<Void> handleMessage(String message, Acknowledger acknowledger) {
            return Mono.error(new IllegalStateException("fake message handling error"));
        }

    };

    @Mock
    private SqsAsyncClient sqsClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DefaultSqsMessageChecker sqsMessageChecker;

    @Captor
    private ArgumentCaptor<DeleteMessageRequest> deleteMessageRequestArgumentCaptor;

    @Test
    @DisplayName("should let the listeners acknowledge the messages")
    void acknowledgment() throws Exception {

        var firstMessage = Message.builder()
                .body("the first message")
                .build();
        var secondMessage = Message.builder()
                .body("the second message")
                .build();
        var receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl("the-queue-url")
                .maxNumberOfMessages(10)
                .visibilityTimeout(5)
                .waitTimeSeconds(20)
                .build();
        var receiveMessageResponse = ReceiveMessageResponse.builder()
                .messages(firstMessage, secondMessage)
                .build();
        given(sqsClient.receiveMessage(receiveMessageRequest)).willReturn(completedFuture(receiveMessageResponse));

        given(objectMapper.readValue("the first message", String.class)).willReturn("the first parsed message");
        given(objectMapper.readValue("the second message", String.class)).willReturn("the second parsed message");

        var deleteFirstMessageRequest = DeleteMessageRequest.builder()
                .queueUrl("the-queue-url")
                .receiptHandle(firstMessage.receiptHandle())
                .build();
        var deleteFirstMessageResponse = DeleteMessageResponse.builder()
                .build();
        given(sqsClient.deleteMessage(deleteFirstMessageRequest)).willReturn(completedFuture(deleteFirstMessageResponse));

        var deleteSecondMessageRequest = DeleteMessageRequest.builder()
                .queueUrl("the-queue-url")
                .receiptHandle(secondMessage.receiptHandle())
                .build();
        var deleteSecondMessageResponse = DeleteMessageResponse.builder()
                .build();
        given(sqsClient.deleteMessage(deleteSecondMessageRequest)).willReturn(completedFuture(deleteSecondMessageResponse));

        sqsMessageChecker.checkForMessages("the-queue-url", acknowledgingListener).block();

        verify(sqsClient, times(2)).deleteMessage(deleteMessageRequestArgumentCaptor.capture());
        var allCapturedDeleteMessageRequests = deleteMessageRequestArgumentCaptor.getAllValues();
        assertThat(allCapturedDeleteMessageRequests).containsExactlyInAnyOrder(
                deleteFirstMessageRequest,
                deleteSecondMessageRequest
        );

    }

    @Test
    @DisplayName("should not swallow any error")
    void error() throws Exception {

        var message = Message.builder()
                .body("the message")
                .build();
        var receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl("the-queue-url")
                .maxNumberOfMessages(10)
                .visibilityTimeout(5)
                .waitTimeSeconds(20)
                .build();
        var receiveMessageResponse = ReceiveMessageResponse.builder()
                .messages(message)
                .build();
        given(sqsClient.receiveMessage(receiveMessageRequest)).willReturn(completedFuture(receiveMessageResponse));

        given(objectMapper.readValue("the message", String.class)).willReturn("the parsed message");

        var thrownException = assertThrows(
                IllegalStateException.class,
                () -> sqsMessageChecker.checkForMessages("the-queue-url", failingListener).block()
        );
        assertThat(thrownException.getMessage()).isEqualTo("fake message handling error");

    }

}
