package com.qudini.reactive.logging.web;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public interface CorrelationIdForwarder {

    Mono<WebClient.RequestBodySpec> forward(WebClient.RequestBodySpec webClient);

    Mono<WebClient.RequestHeadersSpec<?>> forward(WebClient.RequestHeadersSpec<?> webClient);

}
