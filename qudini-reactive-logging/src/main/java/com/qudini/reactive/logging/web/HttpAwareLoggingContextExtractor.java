package com.qudini.reactive.logging.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.util.Map;

@RequiredArgsConstructor
public final class HttpAwareLoggingContextExtractor implements LoggingContextExtractor {

    @Override
    public Mono<Map<String, String>> extract(ServerHttpRequest request) {
        return Mono.just(Map.of(
                "request", request.getMethod() + " " + request.getPath().pathWithinApplication().value(),
                "user_agent", String.join(", ", request.getHeaders().getOrEmpty("User-Agent"))
        ));
    }

}
