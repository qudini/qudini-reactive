package com.qudini.reactive.security.web;

import com.qudini.reactive.security.support.Unauthenticated;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.security.core.context.ReactiveSecurityContextHolder.withAuthentication;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccessDeniedExceptionHandlingFilter")
class AccessDeniedExceptionHandlingFilterTest {

    private static final UsernamePasswordAuthenticationToken AUTHENTICATION = new UsernamePasswordAuthenticationToken("foo", "foo", Set.of());

    @Mock
    ServerWebExchange exchange;

    @Mock
    WebFilterChain chain;

    @InjectMocks
    AccessDeniedExceptionHandlingFilter filter;

    @Test
    void noAuthentication() {
        given(chain.filter(exchange)).willReturn(Mono.error(new AccessDeniedException("test")));
        var completion = filter.filter(exchange, chain);
        var thrown = assertThrows(ResponseStatusException.class, completion::block);
        assertThat(thrown.getStatus()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    void unauthenticated() {
        given(chain.filter(exchange)).willReturn(Mono.error(new AccessDeniedException("test")));
        var completion = filter.filter(exchange, chain).contextWrite(withAuthentication(Unauthenticated.INSTANCE));
        var thrown = assertThrows(ResponseStatusException.class, completion::block);
        assertThat(thrown.getStatus()).isEqualTo(UNAUTHORIZED);
    }

    @Test
    void authenticated() {
        given(chain.filter(exchange)).willReturn(Mono.error(new AccessDeniedException("test")));
        var completion = filter.filter(exchange, chain).contextWrite(withAuthentication(AUTHENTICATION));
        var thrown = assertThrows(AuthenticatedResponseStatusException.class, completion::block);
        assertThat(thrown.getStatus()).isEqualTo(FORBIDDEN);
        assertThat(thrown.getLoggingContext()).isEqualTo(Map.of("principal", "foo"));
    }

    @Test
    void noException() {
        given(chain.filter(exchange)).willReturn(Mono.empty());
        filter.filter(exchange, chain).block();
        filter.filter(exchange, chain).contextWrite(withAuthentication(Unauthenticated.INSTANCE)).block();
        filter.filter(exchange, chain).contextWrite(withAuthentication(AUTHENTICATION)).block();
    }

}
