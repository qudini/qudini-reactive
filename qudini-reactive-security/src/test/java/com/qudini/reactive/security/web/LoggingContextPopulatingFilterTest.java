package com.qudini.reactive.security.web;

import com.qudini.reactive.logging.Log;
import com.qudini.reactive.security.support.Unauthenticated;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.core.context.ReactiveSecurityContextHolder.withAuthentication;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoggingContextPopulatingFilter")
public class LoggingContextPopulatingFilterTest {

    private static final UsernamePasswordAuthenticationToken AUTHENTICATION = new UsernamePasswordAuthenticationToken("foo", "foo", Set.of());

    @Mock
    ServerWebExchange exchange;

    @Mock
    WebFilterChain chain;

    @InjectMocks
    LoggingContextPopulatingFilter filter;

    @Test
    void noAuthentication() {
        var loggingContext = new AtomicReference<Map<String, String>>();
        var loggingContextCatcher = Mono
                .deferContextual(Mono::just)
                .map(context -> context.<Map<String, String>>getOrEmpty(Log.LOGGING_MDC_KEY))
                .flatMap(Mono::justOrEmpty)
                .doOnNext(loggingContext::set)
                .then();
        given(chain.filter(exchange)).willReturn(loggingContextCatcher);
        filter.filter(exchange, chain).block();
        assertThat(loggingContext.get()).isNull();
    }

    @Test
    void unauthenticated() {
        var loggingContext = new AtomicReference<Map<String, String>>();
        var loggingContextCatcher = Mono
                .deferContextual(Mono::just)
                .map(context -> context.<Map<String, String>>getOrEmpty(Log.LOGGING_MDC_KEY))
                .flatMap(Mono::justOrEmpty)
                .doOnNext(loggingContext::set)
                .then();
        given(chain.filter(exchange)).willReturn(loggingContextCatcher);
        filter.filter(exchange, chain).contextWrite(withAuthentication(Unauthenticated.INSTANCE)).block();
        assertThat(loggingContext.get()).isNull();
    }

    @Test
    void authenticated() {
        var loggingContext = new AtomicReference<Map<String, String>>();
        var loggingContextCatcher = Mono
                .deferContextual(Mono::just)
                .map(context -> context.<Map<String, String>>getOrEmpty(Log.LOGGING_MDC_KEY))
                .flatMap(Mono::justOrEmpty)
                .doOnNext(loggingContext::set)
                .then();
        given(chain.filter(exchange)).willReturn(loggingContextCatcher);
        filter.filter(exchange, chain).contextWrite(withAuthentication(AUTHENTICATION)).block();
        assertThat(loggingContext.get()).containsExactlyInAnyOrderEntriesOf(Map.of("principal", "foo"));
    }

}
