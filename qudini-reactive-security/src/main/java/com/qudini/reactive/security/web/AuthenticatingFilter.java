package com.qudini.reactive.security.web;

import com.qudini.reactive.logging.Log;
import com.qudini.reactive.security.support.Unauthenticated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toUnmodifiableSet;
import static org.springframework.security.core.context.ReactiveSecurityContextHolder.withAuthentication;

@Slf4j
@RequiredArgsConstructor
public final class AuthenticatingFilter implements WebFilter {

    private final Collection<AuthenticationService<?>> authenticationServices;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return findAuthentication(exchange)
                .defaultIfEmpty(Unauthenticated.INSTANCE)
                .flatMap(authentication -> filter(exchange, chain, authentication));
    }

    private Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain, Authentication authentication) {
        return chain
                .filter(exchange)
                .contextWrite(withAuthentication(authentication));
    }

    private Mono<Authentication> findAuthentication(ServerWebExchange exchange) {
        return Flux
                .fromIterable(authenticationServices)
                .flatMap(authenticationService -> findAuthentication(authenticationService, exchange))
                .collect(toUnmodifiableSet())
                .flatMap(Log.then(this::chooseAuthentication))
                .flatMap(Mono::justOrEmpty);
    }

    private Mono<? extends Authentication> findAuthentication(AuthenticationService<? extends Authentication> authenticationService, ServerWebExchange exchange) {
        return authenticationService
                .authenticate(exchange)
                .doOnEach(Log.onError(e -> log.warn("An error occurred while authenticating", e)))
                .onErrorResume(e -> Mono.empty());
    }

    private Optional<Authentication> chooseAuthentication(Set<? extends Authentication> authentications) {
        var iterator = authentications.iterator();
        if (!iterator.hasNext()) {
            return Optional.empty();
        }
        var authentication = iterator.next();
        if (iterator.hasNext()) {
            log.warn("Unable to authenticate, found {} valid authentications, request will be considered unauthenticated: {}", authentications.size(), authentications);
            return Optional.empty();
        }
        return Optional.of(authentication);
    }

}
