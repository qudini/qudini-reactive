package com.qudini.reactive.sqs;

import com.qudini.reactive.sqs.message.Acknowledger;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

/**
 * <p>Non-blocking SQS listener.</p>
 * <p>Example:</p>
 * <pre>{@literal
 * @Component
 * public class YourSqsListener implements SqsListener {
 *
 *     @Override
 *     public String getQueueName() {
 *         return "your-sqs-queue";
 *     }
 *
 *     @Override
 *     public Class<T> getMessageType() {
 *         return YourMessage.class
 *     }
 *
 *     @Override
 *     public Mono<Void> handleMessage(YourMessage message, Acknowledger acknowledger) {
 *         return Log
 *             .thenMono(() -> {
 *                 log.info("Received a new SQS message: {}", message);
 *                 return process(message);
 *             })
 *             .then(acknowledger.acknowledge());
 *     }
 *
 * }
 * }</pre>
 */
public interface SqsListener<T> {

    /**
     * <p>The queue name to listen to (only one listener per queue allowed).</p>
     */
    String getQueueName();

    /**
     * <p>Builds the {@link ReceiveMessageRequest} for the given queue URL.</p>
     */
    default ReceiveMessageRequest buildReceiveMessageRequest(String queueUrl) {
        return ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .visibilityTimeout(5)
                .waitTimeSeconds(20)
                .build();
    }

    /**
     * <p>The type that must be used by Jackson when deserialising an incoming SQS message.</p>
     */
    Class<T> getMessageType();

    /**
     * <p>Handles the incoming SQS message.</p>
     */
    Mono<Void> handleMessage(T message, Acknowledger acknowledger);

}
