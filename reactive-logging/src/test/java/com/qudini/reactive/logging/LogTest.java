package com.qudini.reactive.logging;

import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class LogTest {

    private static final String MDC_CONTEXT_MAP_KEY = "MdcContextMap";

    @Test
    void monoSupplier() {
        var mdcValue = Log
                .mono(() -> Mono.just(MDC.get("key")))
                .subscriberContext(Context.of(MDC_CONTEXT_MAP_KEY, Map.of("key", "value")))
                .block();
        assertThat(mdcValue).isEqualTo("value");
    }

    @Test
    void fluxSupplier() {
        var mdcValue = Log
                .flux(() -> Flux.just(MDC.get("key")))
                .collectList()
                .subscriberContext(Context.of(MDC_CONTEXT_MAP_KEY, Map.of("key", "value")))
                .block();
        assertThat(mdcValue).isEqualTo(List.of("value"));
    }

    @Test
    void monoMapper() {
        var mdcValue = new AtomicReference<>();
        var value = Mono
                .just(42)
                .flatMap(Log.mono(i -> {
                    mdcValue.set(MDC.get("key"));
                    return Mono.just(i);
                }))
                .subscriberContext(Context.of(MDC_CONTEXT_MAP_KEY, Map.of("key", "value")))
                .block();
        assertThat(value).isEqualTo(42);
        assertThat(mdcValue.get()).isEqualTo("value");
    }

    @Test
    void fluxMapper() {
        var mdcValue = new AtomicReference<>();
        var value = Mono
                .just(42)
                .flatMapMany(Log.flux(i -> {
                    mdcValue.set(MDC.get("key"));
                    return Flux.just(i);
                }))
                .collectList()
                .subscriberContext(Context.of(MDC_CONTEXT_MAP_KEY, Map.of("key", "value")))
                .block();
        assertThat(value).isEqualTo(List.of(42));
        assertThat(mdcValue.get()).isEqualTo("value");
    }

    @Test
    void next() {
        var value = new AtomicReference<>();
        var mdcValue = new AtomicReference<>();
        Mono
                .just(42)
                .doOnEach(Log.next(i -> {
                    value.set(i);
                    mdcValue.set(MDC.get("key"));
                }))
                .subscriberContext(Context.of(MDC_CONTEXT_MAP_KEY, Map.of("key", "value")))
                .block();
        assertThat(value.get()).isEqualTo(42);
        assertThat(mdcValue.get()).isEqualTo("value");
    }

    @Test
    void error() {
        var value = new AtomicReference<>();
        var mdcValue = new AtomicReference<>();
        var exception = new Exception();
        Mono
                .error(exception)
                .doOnEach(Log.error(error -> {
                    value.set(error);
                    mdcValue.set(MDC.get("key"));
                }))
                .onErrorResume(x -> Mono.empty())
                .subscriberContext(Context.of(MDC_CONTEXT_MAP_KEY, Map.of("key", "value")))
                .block();
        assertThat(value.get()).isEqualTo(exception);
        assertThat(mdcValue.get()).isEqualTo("value");
    }

    @Test
    void errorInstanceOf() {
        var value = new AtomicReference<>();
        var mdcValue = new AtomicReference<>();
        var exception = new IllegalArgumentException();
        Mono
                .error(exception)
                .doOnEach(Log.error(RuntimeException.class, error -> {
                    value.set(error);
                    mdcValue.set(MDC.get("key"));
                }))
                .onErrorResume(x -> Mono.empty())
                .subscriberContext(Context.of(MDC_CONTEXT_MAP_KEY, Map.of("key", "value")))
                .block();
        assertThat(value.get()).isEqualTo(exception);
        assertThat(mdcValue.get()).isEqualTo("value");
    }

    @Test
    void errorNotInstanceOf() {
        var value = new AtomicReference<>();
        var mdcValue = new AtomicReference<>();
        var exception = new IllegalArgumentException();
        Mono
                .error(exception)
                .doOnEach(Log.error(IllegalStateException.class, error -> {
                    value.set(error);
                    mdcValue.set(MDC.get("key"));
                }))
                .onErrorResume(x -> Mono.empty())
                .subscriberContext(Context.of(MDC_CONTEXT_MAP_KEY, Map.of("key", "value")))
                .block();
        assertThat(value.get()).isNull();
        assertThat(mdcValue.get()).isNull();
    }

    @Test
    void complete() {
        var mdcValue = new AtomicReference<>();
        Mono
                .just(42)
                .doOnEach(Log.complete(() -> mdcValue.set(MDC.get("key"))))
                .subscriberContext(Context.of(MDC_CONTEXT_MAP_KEY, Map.of("key", "value")))
                .block();
        assertThat(mdcValue.get()).isEqualTo("value");
    }

}
