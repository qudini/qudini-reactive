package com.qudini.reactive.logging;

import com.qudini.reactive.logging.correlation.CorrelationIdGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Log")
class LogTest {

    @Mock
    private CorrelationIdGenerator correlationIdGenerator;

    @InjectMocks
    private Log log;

    @Test
    @DisplayName("should generate a context with the given correlation id and logging context")
    void useGivenCorrelationId() {
        var context = log.createContext(Optional.of("given"), Map.of("foo", "bar"));
        var mdc = context.<Map<String, String>>get("LOGGING_MDC");
        assertThat(mdc).containsExactlyInAnyOrderEntriesOf(Map.of(
                "correlation_id", "given",
                "foo", "bar"
        ));
        verify(correlationIdGenerator, never()).generate();
    }

    @Test
    @DisplayName("should generate a correlation id if none is given")
    void generateCorrelationId() {
        given(correlationIdGenerator.generate()).willReturn("generated");
        var context = log.createContext(Optional.empty(), Map.of());
        var mdc = context.<Map<String, String>>get("LOGGING_MDC");
        assertThat(mdc).containsExactlyInAnyOrderEntriesOf(Map.of("correlation_id", "generated"));
    }

    @Test
    @DisplayName("should ignore the correlation id in the logging context if any")
    void ignoreCorrelationIdInContext() {
        var context = log.createContext(Optional.of("given"), Map.of(
                "correlation_id", "ignored",
                "foo", "bar"
        ));
        var mdc = context.<Map<String, String>>get("LOGGING_MDC");
        assertThat(mdc).containsExactlyInAnyOrderEntriesOf(Map.of(
                "correlation_id", "given",
                "foo", "bar"
        ));
    }

    @Test
    @DisplayName("should populate the reactive context when using the Mono supplier")
    void monoSupplier() {
        var mdcValue = Log
                .mono(() -> Mono.just(MDC.get("key")))
                .subscriberContext(createContext())
                .block();
        assertThat(mdcValue).isEqualTo("value");
    }

    @Test
    @DisplayName("should populate the reactive context when using the Flux supplier")
    void fluxSupplier() {
        var mdcValue = Log
                .flux(() -> Flux.just(MDC.get("key")))
                .collectList()
                .subscriberContext(createContext())
                .block();
        assertThat(mdcValue).isEqualTo(List.of("value"));
    }

    @Test
    @DisplayName("should populate the reactive context when using the Mono mapper")
    void monoMapper() {
        var mdcValue = new AtomicReference<>();
        var value = Mono
                .just(42)
                .flatMap(Log.mono(i -> {
                    mdcValue.set(MDC.get("key"));
                    return Mono.just(i);
                }))
                .subscriberContext(createContext())
                .block();
        assertThat(value).isEqualTo(42);
        assertThat(mdcValue.get()).isEqualTo("value");
    }

    @Test
    @DisplayName("should populate the reactive context when using the Flux mapper")
    void fluxMapper() {
        var mdcValue = new AtomicReference<>();
        var value = Mono
                .just(42)
                .flatMapMany(Log.flux(i -> {
                    mdcValue.set(MDC.get("key"));
                    return Flux.just(i);
                }))
                .collectList()
                .subscriberContext(createContext())
                .block();
        assertThat(value).isEqualTo(List.of(42));
        assertThat(mdcValue.get()).isEqualTo("value");
    }

    @Test
    @DisplayName("should populate the reactive context when using the next hook")
    void next() {
        var value = new AtomicReference<>();
        var mdcValue = new AtomicReference<>();
        Mono
                .just(42)
                .doOnEach(Log.next(i -> {
                    value.set(i);
                    mdcValue.set(MDC.get("key"));
                }))
                .subscriberContext(createContext())
                .block();
        assertThat(value.get()).isEqualTo(42);
        assertThat(mdcValue.get()).isEqualTo("value");
    }

    @Test
    @DisplayName("should populate the reactive context when using the error hook")
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
                .subscriberContext(createContext())
                .block();
        assertThat(value.get()).isEqualTo(exception);
        assertThat(mdcValue.get()).isEqualTo("value");
    }

    @Test
    @DisplayName("should populate the reactive context when using the error hook with an instance matching the given type")
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
                .subscriberContext(createContext())
                .block();
        assertThat(value.get()).isEqualTo(exception);
        assertThat(mdcValue.get()).isEqualTo("value");
    }

    @Test
    @DisplayName("should not populate the reactive context when using the error hook with an instance not matching the given type")
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
                .subscriberContext(createContext())
                .block();
        assertThat(value.get()).isNull();
        assertThat(mdcValue.get()).isNull();
    }

    @Test
    @DisplayName("should populate the reactive context when using the complete hook")
    void complete() {
        var mdcValue = new AtomicReference<>();
        Mono
                .just(42)
                .doOnEach(Log.complete(() -> mdcValue.set(MDC.get("key"))))
                .subscriberContext(createContext())
                .block();
        assertThat(mdcValue.get()).isEqualTo("value");
    }

    private Context createContext() {
        return Context.of("LOGGING_MDC", Map.of("key", "value"));
    }

}
