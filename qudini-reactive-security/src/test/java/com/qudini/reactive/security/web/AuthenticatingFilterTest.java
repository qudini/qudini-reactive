package com.qudini.reactive.security.web;

import com.qudini.reactive.security.support.Unauthenticated;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.core.context.ReactiveSecurityContextHolder.withAuthentication;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticatingFilter")
class AuthenticatingFilterTest {

    private static final UsernamePasswordAuthenticationToken FOO_AUTHENTICATION = new UsernamePasswordAuthenticationToken("foo", "foo", Set.of());
    private static final UsernamePasswordAuthenticationToken BAR_AUTHENTICATION = new UsernamePasswordAuthenticationToken("bar", "bar", Set.of());

    @Mock
    private AuthenticationService<UsernamePasswordAuthenticationToken> firstAuthenticationService;

    @Mock
    private AuthenticationService<UsernamePasswordAuthenticationToken> secondAuthenticationService;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private WebFilterChain chain;

    private AuthenticatingFilter filter;

    @BeforeEach
    void prepare() {
        filter = new AuthenticatingFilter(Set.of(firstAuthenticationService, secondAuthenticationService));
    }

    @Test
    @DisplayName("should not authenticate when no authentication is found")
    void shouldSetUnauthenticatedWhenNoAuthentication() {
        given(firstAuthenticationService.authenticate(exchange)).willReturn(Mono.empty());
        given(secondAuthenticationService.authenticate(exchange)).willReturn(Mono.empty());
        verifyAuthentication(Unauthenticated.class);
    }

    @Test
    @DisplayName("should set the returned authentication when only the first service returns one")
    void shouldSetAuthenticationWhenFirstServiceOnly() {
        given(firstAuthenticationService.authenticate(exchange)).willReturn(Mono.just(FOO_AUTHENTICATION));
        given(secondAuthenticationService.authenticate(exchange)).willReturn(Mono.empty());
        verifyAuthentication(FOO_AUTHENTICATION.getClass());
    }

    @Test
    @DisplayName("should set the returned authentication when only the second service returns one")
    void shouldSetAuthenticationWhenSecondServiceOnly() {
        given(firstAuthenticationService.authenticate(exchange)).willReturn(Mono.empty());
        given(secondAuthenticationService.authenticate(exchange)).willReturn(Mono.just(FOO_AUTHENTICATION));
        verifyAuthentication(FOO_AUTHENTICATION.getClass());
    }

    @Test
    @DisplayName("should set the returned authentication when all the returned found ones are equal")
    void shouldSetAuthenticationWhenMultipleIdenticalFound() {
        given(firstAuthenticationService.authenticate(exchange)).willReturn(Mono.just(FOO_AUTHENTICATION));
        given(secondAuthenticationService.authenticate(exchange)).willReturn(Mono.just(FOO_AUTHENTICATION));
        verifyAuthentication(FOO_AUTHENTICATION.getClass());
    }

    @Test
    @DisplayName("should not authenticate when two distinct authentications are found")
    void shouldSetUnauthenticatedWhenTwoDistinctAuthentications() {
        given(firstAuthenticationService.authenticate(exchange)).willReturn(Mono.just(FOO_AUTHENTICATION));
        given(secondAuthenticationService.authenticate(exchange)).willReturn(Mono.just(BAR_AUTHENTICATION));
        verifyAuthentication(Unauthenticated.class);
    }

    @Test
    @DisplayName("should not overwrite any existing authentication")
    void shouldNotOverwriteExisting() {
        var authentication = prepareAuthenticationCatcher();
        var existingAuthentication = new TestingAuthenticationToken("authenticated", "pwd", "role");
        filter.filter(exchange, chain).contextWrite(withAuthentication(existingAuthentication)).block();
        assertThat(authentication.get()).isEqualTo(existingAuthentication);
    }

    @Test
    @DisplayName("should reuse unauthenticated instance if any")
    void shouldReuseExistingUnauthenticated() {
        var authentication = prepareAuthenticationCatcher();
        var existingAuthentication = new TestingAuthenticationToken("unauthenticated", "pwd");
        given(firstAuthenticationService.authenticate(exchange)).willReturn(Mono.empty());
        given(secondAuthenticationService.authenticate(exchange)).willReturn(Mono.empty());
        filter.filter(exchange, chain).contextWrite(withAuthentication(existingAuthentication)).block();
        assertThat(authentication.get()).isEqualTo(existingAuthentication);
    }

    private void verifyAuthentication(Class<? extends Authentication> expectedAuthenticationType) {
        var authentication = prepareAuthenticationCatcher();
        filter.filter(exchange, chain).block();
        assertThat(authentication.get()).isInstanceOf(expectedAuthenticationType);
    }

    private AtomicReference<Authentication> prepareAuthenticationCatcher() {
        var authentication = new AtomicReference<Authentication>();
        var authenticationCatcher = ReactiveSecurityContextHolder
                .getContext()
                .map(SecurityContext::getAuthentication)
                .doOnNext(authentication::set)
                .then();
        given(chain.filter(exchange)).willReturn(authenticationCatcher);
        return authentication;
    }

}
