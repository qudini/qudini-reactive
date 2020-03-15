package com.qudini.reactive.logging.web;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface LoggingContextExtractor {

    /**
     * Extracts a logging context from an HTTP exchange.
     */
    Mono<Map<String, String>> extract(ServerWebExchange exchange);

}
