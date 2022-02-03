package com.qudini.reactive.security.web;

import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface AuthenticationService<A extends Authentication> {

    Mono<A> authenticate(ServerWebExchange exchange);

}
