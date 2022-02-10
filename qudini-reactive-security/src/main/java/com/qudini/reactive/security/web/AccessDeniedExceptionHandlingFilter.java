package com.qudini.reactive.security.web;

import com.qudini.reactive.logging.web.ExceptionHandlingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static com.qudini.reactive.security.support.Authentications.currentAuthenticationName;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RequiredArgsConstructor
public final class AccessDeniedExceptionHandlingFilter implements WebFilter, Ordered {

    // after com.qudini.reactive.logging.web.ExceptionHandlingFilter:
    public static final int ORDER = ExceptionHandlingFilter.ORDER + 10;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain
                .filter(exchange)
                .onErrorResume(AccessDeniedException.class, this::handleException);
    }

    private Mono<Void> handleException(AccessDeniedException e) {
        return currentAuthenticationName()
                .flatMap(principal -> Mono.<Void>error(() -> new AuthenticatedResponseStatusException(principal, FORBIDDEN, e.getMessage(), e)))
                .switchIfEmpty(Mono.error(() -> new ResponseStatusException(UNAUTHORIZED, e.getMessage(), e)));
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

}
