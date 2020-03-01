package com.qudini.reactive.logging.web;

import org.springframework.web.server.ServerWebExchange;

import java.util.Map;

public final class DefaultLoggingContextExtractor implements LoggingContextExtractor {

    @Override
    public Map<String, String> extract(ServerWebExchange exchange) {
        return Map.of();
    }

}
