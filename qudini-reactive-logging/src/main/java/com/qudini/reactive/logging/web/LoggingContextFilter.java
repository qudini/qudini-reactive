package com.qudini.reactive.logging.web;

import com.qudini.reactive.logging.ReactiveLoggingContextCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static com.qudini.reactive.utils.MoreTuples.onBoth;

@RequiredArgsConstructor
public final class LoggingContextFilter implements WebFilter, Ordered {

    private final String correlationIdHeader;

    private final LoggingContextExtractor loggingContextExtractor;

    private final ReactiveLoggingContextCreator reactiveLoggingContextCreator;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return Mono
                .zip(
                        Mono.just(extractCorrelationId(exchange)),
                        loggingContextExtractor.extract(exchange)
                )
                .map(onBoth(reactiveLoggingContextCreator::create))
                .flatMap(context -> chain.filter(exchange).subscriberContext(context));
    }

    private Optional<String> extractCorrelationId(ServerWebExchange exchange) {
        return Optional.ofNullable(exchange.getRequest().getHeaders().getFirst(correlationIdHeader));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
