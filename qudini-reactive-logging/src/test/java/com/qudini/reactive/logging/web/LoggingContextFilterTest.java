package com.qudini.reactive.logging.web;

import com.qudini.reactive.logging.ReactiveLoggingContextCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoggingContextFilter")
public class LoggingContextFilterTest {

    @Mock
    private LoggingContextExtractor loggingContextExtractor;

    @Mock
    private ReactiveLoggingContextCreator reactiveLoggingContextCreator;

    @Mock
    private ServerWebExchange exchange;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private HttpHeaders headers;

    @Mock
    private WebFilterChain chain;

    private LoggingContextFilter filter;

    @BeforeEach
    void prepareMocks() {
        filter = new LoggingContextFilter("header", loggingContextExtractor, reactiveLoggingContextCreator);
    }

    @Test
    @DisplayName("should forward the correlation id to a request without a body")
    void forwardWithoutBodyWithCorrelationId() {
        var request = WebClient.create().get();
        var updatedRequest = filter
                .forward(request)
                .subscriberContext(Context.of("LOGGING_MDC", Map.of("correlation_id", "correlation id")))
                .block();
        var called = new AtomicBoolean();
        assertThat(updatedRequest).isNotNull();
        updatedRequest.headers(headers -> {
            assertThat(headers.get("header")).isEqualTo(List.of("correlation id"));
            called.set(true);
        });
        assertThat(called.get()).isTrue();
    }

    @Test
    @DisplayName("should not forward the correlation id if not found to a request without a body")
    void forwardWithoutBodyWithoutCorrelationId() {
        var request = WebClient.create().get();
        var updatedRequest = filter
                .forward(request)
                .block();
        var called = new AtomicBoolean();
        assertThat(updatedRequest).isNotNull();
        updatedRequest.headers(headers -> {
            assertThat(headers.containsKey("header")).isFalse();
            called.set(true);
        });
        assertThat(called.get()).isTrue();
    }

    @Test
    @DisplayName("should forward the correlation id to a request with a body")
    void forwardWithBodyWithCorrelationId() {
        var request = WebClient.create().post();
        var updatedRequest = filter
                .forward(request)
                .subscriberContext(Context.of("LOGGING_MDC", Map.of("correlation_id", "correlation id")))
                .block();
        var called = new AtomicBoolean();
        assertThat(updatedRequest).isNotNull();
        updatedRequest.headers(headers -> {
            assertThat(headers.get("header")).isEqualTo(List.of("correlation id"));
            called.set(true);
        });
        assertThat(called.get()).isTrue();
    }

    @Test
    @DisplayName("should not forward the correlation id to a request with a body")
    void forwardWithBodyWithoutCorrelationId() {
        var request = WebClient.create().post();
        var updatedRequest = filter
                .forward(request)
                .block();
        var called = new AtomicBoolean();
        assertThat(updatedRequest).isNotNull();
        updatedRequest.headers(headers -> {
            assertThat(headers.containsKey("header")).isFalse();
            called.set(true);
        });
        assertThat(called.get()).isTrue();
    }

    @Test
    @DisplayName("should populate the reactive context")
    void populateReactiveContext() {
        var contextValue = new AtomicReference<>();
        var reactiveContext = Context.of("foo", "bar");
        var filtered = Mono
                .subscriberContext()
                .doOnNext(context -> contextValue.set(context.get("foo")))
                .then();
        given(exchange.getRequest()).willReturn(request);
        given(request.getHeaders()).willReturn(headers);
        given(headers.getFirst("header")).willReturn(null);
        given(loggingContextExtractor.extract(exchange)).willReturn(Mono.just(Map.of()));
        given(reactiveLoggingContextCreator.create(any(), any())).willReturn(reactiveContext);
        given(chain.filter(exchange)).willReturn(filtered);
        filter.filter(exchange, chain).block();
        assertThat(contextValue.get()).isEqualTo("bar");
    }

    @Test
    @DisplayName("should run as early as possible")
    void highestPrecedence() {
        assertThat(filter.getOrder()).isEqualTo(Ordered.HIGHEST_PRECEDENCE);
    }

}
