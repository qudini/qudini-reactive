package com.qudini.reactive.logging;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Log")
class LogTest {

    private static final String MDC_CONTEXT_MAP_KEY = "MdcContextMap";

    @Test
    void just() {
        var mdcValue = Log
                .just(() -> MDC.get("key"))
                .subscriberContext(Context.of(MDC_CONTEXT_MAP_KEY, Map.of("key", "value")))
                .block();
        assertThat(mdcValue).isEqualTo("value");
    }

    @Test
    void justOrEmpty() {
        var mdcValue = Log
                .justOrEmpty(() -> Optional.of(MDC.get("key")))
                .subscriberContext(Context.of(MDC_CONTEXT_MAP_KEY, Map.of("key", "value")))
                .block();
        assertThat(mdcValue).isEqualTo("value");
    }

    @Test
    void defer() {
        var mdcValue = Log
                .defer(() -> Mono.just(MDC.get("key")))
                .subscriberContext(Context.of(MDC_CONTEXT_MAP_KEY, Map.of("key", "value")))
                .block();
        assertThat(mdcValue).isEqualTo("value");
    }

    @Test
    void map() {
        var mdcValue = new AtomicReference<>();
        var value = Mono
                .just(42)
                .flatMap(Log.map(i -> {
                    mdcValue.set(MDC.get("key"));
                    return i;
                }))
                .subscriberContext(Context.of(MDC_CONTEXT_MAP_KEY, Map.of("key", "value")))
                .block();
        assertThat(value).isEqualTo(42);
        assertThat(mdcValue.get()).isEqualTo("value");
    }

    @Test
    void flatMap() {
        var mdcValue = new AtomicReference<>();
        var value = Mono
                .just(42)
                .flatMap(Log.flatMap(i -> {
                    mdcValue.set(MDC.get("key"));
                    return Mono.just(i);
                }))
                .subscriberContext(Context.of(MDC_CONTEXT_MAP_KEY, Map.of("key", "value")))
                .block();
        assertThat(value).isEqualTo(42);
        assertThat(mdcValue.get()).isEqualTo("value");
    }

    @Test
    void flatMapMany() {
        var mdcValue = new AtomicReference<>();
        var value = Mono
                .just(42)
                .flatMapMany(Log.flatMapMany(i -> {
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
    void flatMapIterable() {
        var mdcValue = new AtomicReference<>();
        var value = Mono
                .just(42)
                .flatMapMany(Log.flatMapIterable(i -> {
                    mdcValue.set(MDC.get("key"));
                    return List.of(i);
                }))
                .collectList()
                .subscriberContext(Context.of(MDC_CONTEXT_MAP_KEY, Map.of("key", "value")))
                .block();
        assertThat(value).isEqualTo(List.of(42));
        assertThat(mdcValue.get()).isEqualTo("value");
    }

    @Test
    void filter() {
        var mdcValue = new AtomicReference<>();
        var value = Mono
                .just(42)
                .filterWhen(Log.map(i -> {
                    mdcValue.set(MDC.get("key"));
                    return true;
                }))
                .subscriberContext(Context.of(MDC_CONTEXT_MAP_KEY, Map.of("key", "value")))
                .block();
        assertThat(value).isEqualTo(42);
        assertThat(mdcValue.get()).isEqualTo("value");
    }

    @Test
    void onStart() {
        var mdcValue = new AtomicReference<>();
        Log
                .onStart(() -> mdcValue.set(MDC.get("key")))
                .subscriberContext(Context.of(MDC_CONTEXT_MAP_KEY, Map.of("key", "value")))
                .block();
        assertThat(mdcValue.get()).isEqualTo("value");
    }

    @Test
    void onNext() {
        var value = new AtomicReference<>();
        var mdcValue = new AtomicReference<>();
        Mono
                .just(42)
                .doOnEach(Log.onNext(i -> {
                    value.set(i);
                    mdcValue.set(MDC.get("key"));
                }))
                .subscriberContext(Context.of(MDC_CONTEXT_MAP_KEY, Map.of("key", "value")))
                .block();
        assertThat(value.get()).isEqualTo(42);
        assertThat(mdcValue.get()).isEqualTo("value");
    }

    @Test
    void onError() {
        var value = new AtomicReference<>();
        var mdcValue = new AtomicReference<>();
        var exception = new Exception();
        Mono
                .error(exception)
                .doOnEach(Log.onError(error -> {
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
    void onErrorInstanceOf() {
        var value = new AtomicReference<>();
        var mdcValue = new AtomicReference<>();
        var exception = new IllegalArgumentException();
        Mono
                .error(exception)
                .doOnEach(Log.onError(RuntimeException.class, error -> {
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
    void onErrorNotInstanceOf() {
        var value = new AtomicReference<>();
        var mdcValue = new AtomicReference<>();
        var exception = new IllegalArgumentException();
        Mono
                .error(exception)
                .doOnEach(Log.onError(IllegalStateException.class, error -> {
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
    void onComplete() {
        var mdcValue = new AtomicReference<>();
        Mono
                .just(42)
                .doOnEach(Log.onComplete(() -> mdcValue.set(MDC.get("key"))))
                .subscriberContext(Context.of(MDC_CONTEXT_MAP_KEY, Map.of("key", "value")))
                .block();
        assertThat(mdcValue.get()).isEqualTo("value");
    }

}
