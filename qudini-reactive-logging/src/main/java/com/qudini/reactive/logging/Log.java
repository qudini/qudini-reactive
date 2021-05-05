package com.qudini.reactive.logging;

import com.qudini.reactive.logging.correlation.CorrelationIdGenerator;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.slf4j.MDC;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.core.publisher.SignalType;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Collections.unmodifiableMap;

@RequiredArgsConstructor
public final class Log implements ReactiveLoggingContextCreator {

    public static final String LOGGING_MDC_KEY = "LOGGING_MDC";

    public static final String CORRELATION_ID_KEY = "correlation_id";

    private final CorrelationIdGenerator correlationIdGenerator;

    @Override
    public ContextView create(Optional<String> correlationId, Map<String, String> loggingContext) {
        Map<String, String> mdc = new HashMap<>(loggingContext);
        mdc.put(CORRELATION_ID_KEY, correlationId.orElseGet(correlationIdGenerator::generate));
        return Context.of(LOGGING_MDC_KEY, unmodifiableMap(mdc));
    }

    /**
     * <p>Runs the given supplier with the MDC available.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Mono<Integer> example() {
     *     return Log.then(() -> {
     *         log.debug("foobar");
     *         return 42;
     *     });
     * }
     * }</pre>
     */
    public static <R> Mono<R> then(Supplier<R> supplier) {
        return context().map(context -> withContext(context, supplier));
    }

    /**
     * <p>Runs the given supplier with the MDC available.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Mono<Integer> example() {
     *     return Log.thenFuture(() -> {
     *         log.debug("foobar");
     *         return CompletableFuture.completedFuture(42);
     *     });
     * }
     * }</pre>
     */
    public static <R> Mono<R> thenFuture(Supplier<CompletableFuture<R>> supplier) {
        return context().flatMap(content -> Mono.fromFuture(withContext(content, supplier)));
    }

    /**
     * <p>Runs the given supplier with the MDC available.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Mono<Integer> example() {
     *     return Log.thenMono(() -> {
     *         log.debug("foobar");
     *         return Mono.just(42);
     *     });
     * }
     * }</pre>
     */
    public static <R> Mono<R> thenMono(Supplier<Mono<R>> supplier) {
        return context().flatMap(context -> withContext(context, supplier));
    }

    /**
     * <p>Runs the given supplier with the MDC available.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Flux<Integer> example() {
     *     return Log.thenIterable(() -> {
     *         log.debug("foobar");
     *         return List.of(42);
     *     });
     * }
     * }</pre>
     */
    public static <R> Flux<R> thenIterable(Supplier<Iterable<R>> supplier) {
        return context().flatMapIterable(context -> withContext(context, supplier));
    }

    /**
     * <p>Runs the given supplier with the MDC available.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Flux<Integer> example() {
     *     return Log.thenFlux(() -> {
     *         log.debug("foobar");
     *         return Flux.fromStream(Stream.of(42));
     *     });
     * }
     * }</pre>
     */
    public static <R> Flux<R> thenFlux(Supplier<Publisher<R>> supplier) {
        return context().flatMapMany(context -> withContext(context, supplier));
    }

    /**
     * <p>Runs the given mapper with the MDC available.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Mono<Integer> example(Mono<String> mono) {
     *     return mono.flatMap(Log.then(s -> {
     *         log.debug("s:{}", s);
     *         return 42;
     *     }));
     * }
     * }</pre>
     */
    public static <T, R> Function<T, Mono<R>> then(Function<T, R> mapper) {
        return value -> context().map(context -> withContext(context, () -> mapper.apply(value)));
    }

    /**
     * <p>Runs the given mapper with the MDC available.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Mono<Integer> example(Mono<String> mono) {
     *     return mono.flatMap(Log.thenMono(s -> {
     *         log.debug("s:{}", s);
     *         return Mono.just(42);
     *     }));
     * }
     * }</pre>
     */
    public static <T, R> Function<T, Mono<R>> thenMono(Function<T, Mono<R>> mapper) {
        return value -> context().flatMap(context -> withContext(context, () -> mapper.apply(value)));
    }

    /**
     * <p>Runs the given mapper with the MDC available.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Flux<Integer> example(Mono<String> mono) {
     *     return mono.flatMapMany(Log.thenIterable(s -> {
     *         log.debug("s:{}", s);
     *         return List.of(42);
     *     }));
     * }
     * }</pre>
     */
    public static <T, R> Function<T, Flux<R>> thenIterable(Function<T, Iterable<R>> mapper) {
        return value -> context().flatMapIterable(context -> withContext(context, () -> mapper.apply(value)));
    }

    /**
     * <p>Runs the given mapper with the MDC available.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Flux<Integer> example(Mono<String> mono) {
     *     return mono.flatMapMany(Log.thenFlux(s -> {
     *         log.debug("s:{}", s);
     *         return Flux.fromStream(Stream.of(42));
     *     }));
     * }
     * }</pre>
     */
    public static <T, R> Function<T, Flux<R>> thenFlux(Function<T, Publisher<R>> mapper) {
        return value -> context().flatMapMany(context -> withContext(context, () -> mapper.apply(value)));
    }

    /**
     * <p>Runs the given consumer with the MDC available {@link SignalType#ON_NEXT}.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Mono<String> example(Mono<String> mono) {
     *     return mono.doOnEach(Log.onNext(s -> log.debug("s:{}", s)));
     * }
     * }</pre>
     */
    public static <T> Consumer<Signal<T>> onNext(Consumer<T> consumer) {
        return on(Signal::isOnNext, (value, throwable) -> consumer.accept(value));
    }

    /**
     * <p>Runs the given consumer with the MDC available {@link SignalType#ON_ERROR}.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Mono<String> example(Mono<String> mono) {
     *     return mono.doOnEach(Log.onError(e -> log.debug("An error occurred", e)));
     * }
     * }</pre>
     */
    public static <T> Consumer<Signal<T>> onError(Consumer<Throwable> consumer) {
        return on(Signal::isOnError, (value, throwable) -> consumer.accept(throwable));
    }

    /**
     * <p>Runs the given consumer with the MDC available {@link SignalType#ON_ERROR}
     * if the error matches the given type.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Mono<String> example(Mono<String> mono) {
     *     return mono.doOnEach(Log.onError(YourException.class, e -> log.debug("Your exception occurred", e)));
     * }
     * }</pre>
     */
    public static <T, E extends Throwable> Consumer<Signal<T>> onError(Class<E> throwableClass, Consumer<E> consumer) {
        return on(Signal::isOnError, (value, throwable) -> {
            if (throwableClass.isInstance(throwable)) {
                consumer.accept((E) throwable);
            }
        });
    }

    /**
     * <p>Runs the given consumer with the MDC available {@link SignalType#ON_COMPLETE}.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Mono<String> example(Mono<String> mono) {
     *     return mono.doOnEach(Log.onComplete(() -> log.debug("Completed")));
     * }
     * }</pre>
     */
    public static <T> Consumer<Signal<T>> onComplete(Runnable runnable) {
        return on(Signal::isOnComplete, (value, throwable) -> runnable.run());
    }

    /**
     * <p>Runs the given consumer with the MDC available if the given predicate matches.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Mono<String> example(Mono<String> mono) {
     *     return mono.doOnEach(Log.on(Signal::isOnSubscribe, (value, error) -> log.debug("subscribed with value:{} error:{}", value, error)));
     * }
     * }</pre>
     */
    public static <T> Consumer<Signal<T>> on(Predicate<Signal<T>> event, BiConsumer<T, Throwable> logger) {
        return signal -> {
            if (event.test(signal)) {
                withContext(signal.getContextView(), () -> logger.accept(signal.get(), signal.getThrowable()));
            }
        };
    }

    /**
     * <p>Runs the given runnable with the MDC available
     * after having been extracted from the given reactive context.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * void example(Context context) {
     *     Log.withContext(context, () -> log.debug("foobar"));
     * }
     * }</pre>
     */
    public static void withContext(ContextView context, Runnable runnable) {
        withContext(context, () -> {
            runnable.run();
            return null;
        });
    }

    /**
     * <p>Runs the given supplier with the MDC available
     * after having been extracted from the given reactive context.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * int example(Context context) {
     *     return Log.withContext(context, () -> {
     *         log.debug("foobar");
     *         return 42;
     *     });
     * }
     * }</pre>
     */
    public static <R> R withContext(ContextView context, Supplier<R> supplier) {
        try {
            Map<String, String> mdc = context.getOrDefault(LOGGING_MDC_KEY, Map.of());
            MDC.setContextMap(mdc);
            return supplier.get();
        } finally {
            MDC.clear();
        }
    }

    /**
     * <p>Returns the correlation id if any.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Mono<Integer> example(Context context) {
     *     return Log
     *             .getCorrelationId()
     *             .defaultIfEmpty("N/A")
     *             .doOnNext(correlationId -> store(correlationId))
     *             .then(Mono.just(42));
     * }
     * }</pre>
     */
    public static Mono<String> getCorrelationId() {
        return thenMono(() -> Mono.justOrEmpty(MDC.get(CORRELATION_ID_KEY)));
    }

    private static Mono<ContextView> context() {
        return Mono.deferContextual(Mono::just);
    }

}
