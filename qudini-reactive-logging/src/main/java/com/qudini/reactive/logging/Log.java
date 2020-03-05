package com.qudini.reactive.logging;

import com.qudini.reactive.logging.correlation.CorrelationIdGenerator;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.slf4j.MDC;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.util.context.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Collections.unmodifiableMap;

@RequiredArgsConstructor
public final class Log implements ReactiveLoggingContextCreator {

    private static final String LOGGING_MDC_KEY = "LOGGING_MDC";

    private static final String CORRELATION_ID_KEY = "correlation_id";

    private final CorrelationIdGenerator correlationIdGenerator;

    @Override
    public Context create(Optional<String> correlationId, Map<String, String> loggingContext) {
        Map<String, String> mdc = new HashMap<>(loggingContext);
        mdc.put(CORRELATION_ID_KEY, correlationId.orElseGet(correlationIdGenerator::generate));
        return Context.of(LOGGING_MDC_KEY, unmodifiableMap(mdc));
    }

    public static <R> Mono<R> then(Supplier<R> supplier) {
        return context().map(context -> withContext(context, supplier));
    }

    public static <R> Mono<R> thenMono(Supplier<Mono<R>> supplier) {
        return context().flatMap(context -> withContext(context, supplier));
    }

    public static <R> Flux<R> thenIterable(Supplier<Iterable<R>> supplier) {
        return context().flatMapIterable(context -> withContext(context, supplier));
    }

    public static <R> Flux<R> thenFlux(Supplier<Publisher<R>> supplier) {
        return context().flatMapMany(context -> withContext(context, supplier));
    }

    public static <T, R> Function<T, Mono<R>> then(Function<T, R> mapper) {
        return value -> context().map(context -> withContext(context, () -> mapper.apply(value)));
    }

    public static <T, R> Function<T, Mono<R>> thenMono(Function<T, Mono<R>> mapper) {
        return value -> context().flatMap(context -> withContext(context, () -> mapper.apply(value)));
    }

    public static <T, R> Function<T, Flux<R>> thenIterable(Function<T, Iterable<R>> mapper) {
        return value -> context().flatMapIterable(context -> withContext(context, () -> mapper.apply(value)));
    }

    public static <T, R> Function<T, Flux<R>> thenFlux(Function<T, Publisher<R>> mapper) {
        return value -> context().flatMapMany(context -> withContext(context, () -> mapper.apply(value)));
    }

    public static <T> Consumer<Signal<T>> onNext(Consumer<T> consumer) {
        return on(Signal::isOnNext, (value, throwable) -> consumer.accept(value));
    }

    public static <T> Consumer<Signal<T>> onError(Consumer<Throwable> consumer) {
        return on(Signal::isOnError, (value, throwable) -> consumer.accept(throwable));
    }

    public static <T, E extends Throwable> Consumer<Signal<T>> onError(Class<E> throwableClass, Consumer<E> consumer) {
        return on(Signal::isOnError, (value, throwable) -> {
            if (throwableClass.isInstance(throwable)) {
                consumer.accept((E) throwable);
            }
        });
    }

    public static <T> Consumer<Signal<T>> onComplete(Runnable runnable) {
        return on(Signal::isOnComplete, (value, throwable) -> runnable.run());
    }

    public static <T> Consumer<Signal<T>> on(Predicate<Signal<T>> event, BiConsumer<T, Throwable> logger) {
        return signal -> {
            if (event.test(signal)) {
                withContext(signal.getContext(), () -> logger.accept(signal.get(), signal.getThrowable()));
            }
        };
    }

    public static void withContext(Context context, Runnable runnable) {
        withContext(context, () -> {
            runnable.run();
            return null;
        });
    }

    public static <R> R withContext(Context context, Supplier<R> supplier) {
        try {
            Map<String, String> mdc = context.getOrDefault(LOGGING_MDC_KEY, Map.of());
            MDC.setContextMap(mdc);
            return supplier.get();
        } finally {
            MDC.clear();
        }
    }

    public static Mono<String> getCorrelationId() {
        return thenMono(() -> Mono.justOrEmpty(MDC.get(CORRELATION_ID_KEY)));
    }

    private static Mono<Context> context() {
        return Mono.subscriberContext();
    }

}
