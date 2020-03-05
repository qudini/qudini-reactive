package com.qudini.reactive.sqs.message;

import com.qudini.reactive.sqs.listener.SqsListener;
import reactor.core.publisher.Mono;

public interface SqsMessageChecker {

    Mono<Void> checkForMessages(String queueUrl, SqsListener<?> listener);

}
