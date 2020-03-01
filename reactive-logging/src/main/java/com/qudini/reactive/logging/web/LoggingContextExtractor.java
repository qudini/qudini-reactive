package com.qudini.reactive.logging.web;

import org.springframework.web.server.ServerWebExchange;

import java.util.Map;

public interface LoggingContextExtractor {

    Map<String, String> extract(ServerWebExchange exchange);

}
