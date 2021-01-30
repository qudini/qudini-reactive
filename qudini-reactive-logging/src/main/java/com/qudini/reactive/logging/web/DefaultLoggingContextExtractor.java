package com.qudini.reactive.logging.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@RequiredArgsConstructor
public final class DefaultLoggingContextExtractor implements LoggingContextExtractor {

    public static final LoggingContextExtractor INSTANCE = new DefaultLoggingContextExtractor();

    @Override
    public Mono<Map<String, String>> extract(ServerWebExchange exchange) {
        return Mono.just(Map.of());
    }

}
