package com.qudini.reactive.security.web;

import com.qudini.reactive.logging.web.ExceptionHandlingFilter;
import com.qudini.reactive.security.support.Authentications;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RequiredArgsConstructor
public final class AccessDeniedExceptionTransformingFilter implements WebFilter, Ordered {

    public static final int ORDER = ExceptionHandlingFilter.ORDER + 10;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain
                .filter(exchange)
                .onErrorResume(
                        AccessDeniedException.class,
                        this::handleException
                );
    }

    private Mono<Void> handleException(AccessDeniedException e) {
        return Authentications
                .current()
                .switchIfEmpty(Mono.error(() -> new ResponseStatusException(UNAUTHORIZED, e.getMessage(), e)))
                .then(Mono.error(() -> new ResponseStatusException(FORBIDDEN, e.getMessage(), e)));
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

}
