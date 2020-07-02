package com.qudini.reactive.logging.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.context.Context;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoggingContextFilter")
class DefaultCorrelationIdForwarderTest {

    private DefaultCorrelationIdForwarder forwarder;

    @BeforeEach
    void prepareMocks() {
        forwarder = new DefaultCorrelationIdForwarder("header");
    }

    @Test
    @DisplayName("should forward the correlation id to a request without a body")
    void forwardWithoutBodyWithCorrelationId() {
        var request = WebClient.create().get();
        var updatedRequest = forwarder
                .forwardOn(request)
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
        var updatedRequest = forwarder
                .forwardOn(request)
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
        var updatedRequest = forwarder
                .forwardOn(request)
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
        var updatedRequest = forwarder
                .forwardOn(request)
                .block();
        var called = new AtomicBoolean();
        assertThat(updatedRequest).isNotNull();
        updatedRequest.headers(headers -> {
            assertThat(headers.containsKey("header")).isFalse();
            called.set(true);
        });
        assertThat(called.get()).isTrue();
    }

}
