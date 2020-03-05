package com.qudini.reactive.logging.web;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface LoggingContextExtractor {

    Mono<Map<String, String>> extract(ServerWebExchange exchange);

}
