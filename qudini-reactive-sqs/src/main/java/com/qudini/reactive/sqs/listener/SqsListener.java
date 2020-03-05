package com.qudini.reactive.sqs.listener;

import com.qudini.reactive.sqs.message.Acknowledger;
import reactor.core.publisher.Mono;

public interface SqsListener<T> {

    String getQueueName();

    Class<T> getMessageType();

    Mono<Void> handleMessage(T message, Acknowledger acknowledger);

}
