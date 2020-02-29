package com.qudini.reactive.logging;

import lombok.NoArgsConstructor;
import org.slf4j.MDC;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.util.context.Context;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Collections.emptyMap;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class Log {

    private static final String MDC_CONTEXT_MAP_KEY = "MdcContextMap";

    public static <O> Mono<O> just(Supplier<O> supplier) {
        return context().map(context -> withContext(context, supplier));
    }

    public static <O> Mono<O> justOrEmpty(Supplier<Optional<O>> supplier) {
        return context().flatMap(context -> Mono.justOrEmpty(withContext(context, supplier)));
    }

    public static <O> Mono<O> defer(Supplier<Mono<O>> supplier) {
        return context().flatMap(context -> withContext(context, supplier));
    }

    public static <I, O> Function<I, Mono<O>> map(Function<I, O> mapper) {
        return value -> context().map(context -> withContext(context, () -> mapper.apply(value)));
    }

    public static <I, O> Function<I, Mono<O>> flatMap(Function<I, Mono<O>> mapper) {
        return value -> context().flatMap(context -> withContext(context, () -> mapper.apply(value)));
    }

    public static <I, O> Function<I, Flux<O>> flatMapMany(Function<I, Flux<O>> mapper) {
        return value -> context().flatMapMany(context -> withContext(context, () -> mapper.apply(value)));
    }

    public static <I, O> Function<I, Flux<O>> flatMapIterable(Function<I, Iterable<O>> mapper) {
        return value -> context().flatMapIterable(context -> withContext(context, () -> mapper.apply(value)));
    }

    public static Mono<Void> onStart(Runnable runnable) {
        return context().doOnNext(context -> withContext(context, runnable)).then();
    }

    public static <I> Consumer<Signal<I>> onNext(Consumer<I> consumer) {
        return on(Signal::isOnNext, (value, throwable) -> consumer.accept(value));
    }

    public static <I> Consumer<Signal<I>> onError(Consumer<Throwable> consumer) {
        return on(Signal::isOnError, (value, throwable) -> consumer.accept(throwable));
    }

    public static <I, T extends Throwable> Consumer<Signal<I>> onError(Class<T> throwableClass, Consumer<T> consumer) {
        return on(Signal::isOnError, (value, throwable) -> {
            if (throwableClass.isInstance(throwable)) {
                consumer.accept((T) throwable);
            }
        });
    }

    public static <I> Consumer<Signal<I>> onComplete(Runnable runnable) {
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
            Map<String, String> contextMap = context.getOrDefault(MDC_CONTEXT_MAP_KEY, emptyMap());
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
