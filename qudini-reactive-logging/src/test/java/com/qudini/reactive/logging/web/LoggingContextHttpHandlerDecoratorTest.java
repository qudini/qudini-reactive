package com.qudini.reactive.logging.web;

import com.qudini.reactive.logging.ReactiveLoggingContextCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoggingContextHttpHandlerDecorator")
class LoggingContextHttpHandlerDecoratorTest {

    @Mock
    private HttpHandler delegate;

    @Mock
    private LoggingContextExtractor loggingContextExtractor;

    @Mock
    private ReactiveLoggingContextCreator reactiveLoggingContextCreator;

    @Mock
    private ServerHttpRequest request;

    @Mock
    private ServerHttpResponse response;

    @Mock
    private HttpHeaders headers;

    private LoggingContextHttpHandlerDecorator handler;

    @BeforeEach
    void prepareMocks() {
        handler = new LoggingContextHttpHandlerDecorator(delegate, "X-Header", loggingContextExtractor, reactiveLoggingContextCreator);
    }

    @Test
    @DisplayName("should populate the reactive context")
    void populateReactiveContext() {
        var contextValue = new AtomicReference<Map<String, String>>();
        var reactiveContext = Context.of("LOGGING_MDC", Map.of("correlation_id", "foobar"));
        var filtered = Mono
                .deferContextual(Mono::just)
                .doOnNext(context -> contextValue.set(context.get("LOGGING_MDC")))
                .then();
        given(request.getHeaders()).willReturn(headers);
        given(headers.getFirst("X-Header")).willReturn(null);
        var responseHeaders = new HttpHeaders();
        given(response.getHeaders()).willReturn(responseHeaders);
        given(loggingContextExtractor.extract(request)).willReturn(Mono.just(Map.of()));
        given(reactiveLoggingContextCreator.create(any(), any())).willReturn(reactiveContext);
        given(delegate.handle(request, response)).willReturn(filtered);
        handler.handle(request, response).block();
        assertThat(contextValue.get()).containsExactlyInAnyOrderEntriesOf(Map.of("correlation_id", "foobar"));
        assertThat(responseHeaders.getFirst("X-Header")).isEqualTo("foobar");
    }

}
