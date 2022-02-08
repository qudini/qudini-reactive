package com.qudini.reactive.security.web;

import com.qudini.reactive.logging.web.ExceptionHandlingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.qudini.reactive.logging.Log.withLoggingContext;
import static com.qudini.reactive.security.support.Authentications.currentAuthenticationName;

@RequiredArgsConstructor
public final class LoggingContextPopulatingFilter implements WebFilter, Ordered {

    public static final int ORDER = ExceptionHandlingFilter.ORDER - 10;

    public static final String PRINCIPAL_KEY = "principal";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return currentAuthenticationName()
                .map(name -> Map.of(PRINCIPAL_KEY, name))
                .defaultIfEmpty(Map.of())
                .flatMap(context -> chain.filter(exchange).contextWrite(withLoggingContext(context)));
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

}
