package com.qudini.reactive.security.support;

import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class Authentications {

    public static Mono<Authentication> currentAuthentication() {
        return ReactiveSecurityContextHolder
                .getContext()
                .mapNotNull(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated);
    }

    public static Mono<String> currentAuthenticationName() {
        return currentAuthentication()
                .map(auth -> Optional.ofNullable(auth.getName()).orElse("unknown"));
    }

}
