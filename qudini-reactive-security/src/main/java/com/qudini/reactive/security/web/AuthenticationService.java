package com.qudini.reactive.security.web;

import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface AuthenticationService<A extends Authentication> {

    /**
     * <p>Tries to authenticate the given exchange, returns an empty mono if it could not.</p>
     */
    Mono<A> authenticate(ServerWebExchange exchange);

}
