package com.qudini.reactive.security.web;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public abstract class CookieBasedAuthenticationService<A extends Authentication> implements AuthenticationService<A> {

    private final CsrfVerifier csrfVerifier;

    public final Mono<A> authenticate(ServerWebExchange exchange) {
        return authenticateWithCookies(exchange).filter(x -> csrfVerifier.verify(exchange));
    }

    public abstract Mono<A> authenticateWithCookies(ServerWebExchange exchange);

}
