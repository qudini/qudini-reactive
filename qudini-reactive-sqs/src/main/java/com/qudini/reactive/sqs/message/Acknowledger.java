package com.qudini.reactive.sqs.message;

import reactor.core.publisher.Mono;

@FunctionalInterface
public interface Acknowledger {

    Mono<Void> acknowledge();

}
