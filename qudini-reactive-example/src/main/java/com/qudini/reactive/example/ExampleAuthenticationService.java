package com.qudini.reactive.example;

import com.qudini.reactive.security.web.AuthenticationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.Serial;
import java.util.Collection;
import java.util.Set;

import static com.qudini.reactive.example.ExampleAuthenticationService.ExampleAuthentication;

@Service
public class ExampleAuthenticationService implements AuthenticationService<ExampleAuthentication> {

    @Override
    public Mono<ExampleAuthentication> authenticate(ServerWebExchange exchange) {
        return Mono.just(new ExampleAuthentication());
    }

    public static final class ExampleAuthentication implements Authentication {

        @Serial
        private static final long serialVersionUID = 1L;

        private static final Collection<SimpleGrantedAuthority> AUTHORITIES = Set.of(new SimpleGrantedAuthority("EXAMPLE"));

        @Override
        public Collection<SimpleGrantedAuthority> getAuthorities() {
            return AUTHORITIES;
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getDetails() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return "example";
        }

        @Override
        public boolean isAuthenticated() {
            return true;
        }

        @Override
        public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
            throw new IllegalStateException();
        }

        @Override
        public String getName() {
            return "example";
        }

    }

}
