package com.qudini.reactive.security.web;

import com.qudini.reactive.logging.web.ExceptionHandlingFilter;
import com.qudini.reactive.security.support.Authentications;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

import static com.qudini.reactive.logging.Log.withLoggingContext;

@RequiredArgsConstructor
public final class LoggingContextPopulatingFilter implements WebFilter, Ordered {

    public static final int ORDER = ExceptionHandlingFilter.ORDER - 10;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return Authentications
                .current()
                .map(auth -> Map.of("principal", Optional.ofNullable(auth.getName()).orElse("unknown")))
                .defaultIfEmpty(Map.of())
                .flatMap(context -> chain.filter(exchange).contextWrite(withLoggingContext(context)));
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

}
