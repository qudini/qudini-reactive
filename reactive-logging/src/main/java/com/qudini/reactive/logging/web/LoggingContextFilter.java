package com.qudini.reactive.logging.web;

import com.qudini.reactive.logging.ReactiveContextCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RequiredArgsConstructor
public final class LoggingContextFilter implements WebFilter {

    private final String correlationIdHeader;

    private final LoggingContextExtractor loggingContextExtractor;

    private final ReactiveContextCreator reactiveContextCreator;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var correlationId = extractCorrelationId(exchange);
        var loggingContext = loggingContextExtractor.extract(exchange);
        var context = reactiveContextCreator.create(correlationId, loggingContext);
        return chain.filter(exchange).subscriberContext(context);
    }

    private Optional<String> extractCorrelationId(ServerWebExchange exchange) {
        return Optional.ofNullable(exchange.getRequest().getHeaders().getFirst(correlationIdHeader));
    }

}
