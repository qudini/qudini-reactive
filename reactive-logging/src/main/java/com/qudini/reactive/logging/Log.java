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
public final class Log {

    private static final String LOGGING_MDC_KEY = "LOGGING_MDC";

    private static final String CORRELATION_ID_KEY = "correlation_id";

    private final CorrelationIdGenerator correlationIdGenerator;

    public Context createContext(Optional<String> correlationId, Map<String, String> loggingContext) {
        Map<String, String> mdc = new HashMap<>(loggingContext);
        mdc.put(CORRELATION_ID_KEY, correlationId.orElseGet(correlationIdGenerator::generate));
        return Context.of(LOGGING_MDC_KEY, unmodifiableMap(mdc));
    }

    public static <O> Mono<O> mono(Supplier<Mono<O>> supplier) {
        return context().flatMap(context -> withContext(context, supplier));
    }

    public static <O> Flux<O> flux(Supplier<Publisher<O>> supplier) {
        return context().flatMapMany(context -> withContext(context, supplier));
    }

    public static <I, O> Function<I, Mono<O>> mono(Function<I, Mono<O>> mapper) {
        return value -> context().flatMap(context -> withContext(context, () -> mapper.apply(value)));
    }

    public static <I, O> Function<I, Flux<O>> flux(Function<I, Publisher<O>> mapper) {
        return value -> context().flatMapMany(context -> withContext(context, () -> mapper.apply(value)));
    }

    public static <I> Consumer<Signal<I>> next(Consumer<I> consumer) {
        return on(Signal::isOnNext, (value, throwable) -> consumer.accept(value));
    }

    public static <I> Consumer<Signal<I>> error(Consumer<Throwable> consumer) {
        return on(Signal::isOnError, (value, throwable) -> consumer.accept(throwable));
    }

    public static <I, T extends Throwable> Consumer<Signal<I>> error(Class<T> throwableClass, Consumer<T> consumer) {
        return on(Signal::isOnError, (value, throwable) -> {
            if (throwableClass.isInstance(throwable)) {
                consumer.accept((T) throwable);
            }
        });
    }

    public static <I> Consumer<Signal<I>> complete(Runnable runnable) {
        return on(Signal::isOnComplete, (value, throwable) -> runnable.run());
    }

    public static <I> Consumer<Signal<I>> on(Predicate<Signal<I>> event, BiConsumer<I, Throwable> logger) {
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

    public static <O> O withContext(Context context, Supplier<O> supplier) {
        try {
            Map<String, String> mdc = context.getOrDefault(LOGGING_MDC_KEY, Map.of());
            MDC.setContextMap(mdc);
            return supplier.get();
        } finally {
            MDC.clear();
        }
    }

    private static Mono<Context> context() {
        return Mono.subscriberContext();
    }

}
