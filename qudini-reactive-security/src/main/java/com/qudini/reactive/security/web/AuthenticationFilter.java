package com.qudini.reactive.security.web;

import com.qudini.reactive.logging.Log;
import com.qudini.reactive.security.support.AnonymousAuthentication;
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
public final class AuthenticationFilter implements WebFilter {

    private final Collection<AuthenticationService<?>> authenticationServices;

    public AuthenticationFilter(Collection<AuthenticationService<?>> authenticationServices) {
        this.authenticationServices = authenticationServices;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return findAuthentication(exchange)
                .defaultIfEmpty(new AnonymousAuthentication())
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
                .flatMap(authenticationService -> authenticationService.authenticate(exchange))
                .collect(toUnmodifiableSet())
                .flatMap(Log.then(this::chooseAuthentication))
                .flatMap(Mono::justOrEmpty);
    }

    private Optional<Authentication> chooseAuthentication(Set<? extends Authentication> authentications) {
        var iterator = authentications.iterator();
        if (!iterator.hasNext()) {
            return Optional.empty();
        }
        var authentication = iterator.next();
        if (iterator.hasNext()) {
            log.warn("Unable to authenticate, found {} valid authentications: {}", authentications.size(), authentications);
            return Optional.empty();
        }
        return Optional.of(authentication);
    }

}
