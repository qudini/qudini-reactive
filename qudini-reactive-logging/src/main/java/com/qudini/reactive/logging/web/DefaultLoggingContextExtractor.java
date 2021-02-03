package com.qudini.reactive.logging.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.util.Map;

@RequiredArgsConstructor
public final class DefaultLoggingContextExtractor implements LoggingContextExtractor {

    public static final LoggingContextExtractor INSTANCE = new DefaultLoggingContextExtractor();

    @Override
    public Mono<Map<String, String>> extract(ServerHttpRequest request) {
        return Mono.just(Map.of());
    }

}
