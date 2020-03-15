package com.qudini.reactive.sqs.message;

import com.qudini.reactive.sqs.SqsListener;
import reactor.core.publisher.Mono;

public interface SqsMessageChecker {

    Mono<Void> checkForMessages(String queueUrl, SqsListener<?> listener);

}
