package com.qudini.reactive.logging.web;

import com.qudini.reactive.logging.Log;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public final class DefaultCorrelationIdForwarder implements CorrelationIdForwarder {

    private final String correlationIdHeader;

    @Override
    public Mono<WebClient.RequestHeadersSpec<?>> forwardOn(WebClient.RequestHeadersSpec<?> webClient) {
        return Log
                .getCorrelationId()
                .<WebClient.RequestHeadersSpec<?>>map(correlationId -> webClient.header(correlationIdHeader, correlationId))
                .defaultIfEmpty(webClient);
    }

    @Override
    public Mono<WebClient.RequestBodySpec> forwardOn(WebClient.RequestBodySpec webClient) {
        return Log
                .getCorrelationId()
                .map(correlationId -> webClient.header(correlationIdHeader, correlationId))
                .defaultIfEmpty(webClient);
    }

}
