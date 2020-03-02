package com.qudini.reactive.logging.web;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

public final class DefaultLoggingContextExtractor implements LoggingContextExtractor {

    @Override
    public Mono<Map<String, String>> extract(ServerWebExchange exchange) {
        return Mono.just(Map.of());
    }

}
