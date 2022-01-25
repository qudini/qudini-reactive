package com.qudini.reactive.logging.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public final class HttpAwareLoggingContextExtractor implements LoggingContextExtractor {

    @Override
    public Mono<Map<String, String>> extract(ServerHttpRequest request) {
        Map<String, String> context = new HashMap<>();
        context.put("request", request.getMethodValue() + " " + request.getPath().pathWithinApplication().value());
        context.put("user_agent", String.join(", ", request.getHeaders().getOrEmpty("User-Agent")));
        return Mono.just(Map.copyOf(context));
    }

}
