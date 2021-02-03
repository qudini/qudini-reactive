package com.qudini.reactive.logging.web;

import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface LoggingContextExtractor {

    /**
     * <p>Extracts a logging context from an HTTP exchange.</p>
     */
    Mono<Map<String, String>> extract(ServerHttpRequest request);

}
