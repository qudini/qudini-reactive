package com.qudini.reactive.logging;

import lombok.NoArgsConstructor;
import org.reactivestreams.Publisher;
import org.slf4j.MDC;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.util.context.Context;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Collections.emptyMap;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class Log {

    private static final String CONTEXT_MAP_KEY = "LOGGING_MDC";

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
            Map<String, String> contextMap = context.getOrDefault(CONTEXT_MAP_KEY, emptyMap());
            MDC.setContextMap(contextMap);
            return supplier.get();
        } finally {
            MDC.clear();
        }
    }

    private static Mono<Context> context() {
        return Mono.subscriberContext();
    }

}
