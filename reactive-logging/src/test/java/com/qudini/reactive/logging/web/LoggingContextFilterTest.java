package com.qudini.reactive.logging.web;

import com.qudini.reactive.logging.ReactiveContextCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.Map;
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
    private ReactiveContextCreator reactiveContextCreator;

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
        given(exchange.getRequest()).willReturn(request);
        given(request.getHeaders()).willReturn(headers);
        given(headers.getFirst("header")).willReturn(null);
        given(loggingContextExtractor.extract(exchange)).willReturn(Map.of());
        this.filter = new LoggingContextFilter("header", loggingContextExtractor, reactiveContextCreator);
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
        given(reactiveContextCreator.create(any(), any())).willReturn(reactiveContext);
        given(chain.filter(exchange)).willReturn(filtered);
        filter.filter(exchange, chain).block();
        assertThat(contextValue.get()).isEqualTo("bar");
    }

}
