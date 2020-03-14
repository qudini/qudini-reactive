package com.qudini.reactive.utils;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static lombok.AccessLevel.PRIVATE;

/**
 * Builds functions that can throw checked exceptions.
 */
@NoArgsConstructor(access = PRIVATE)
public final class MoreThrowables {

    @FunctionalInterface
    public interface ThrowableConsumer<T> extends Consumer<T> {

        @Override
        @SneakyThrows
        default void accept(T t) {
            acceptOrThrow(t);
        }

        void acceptOrThrow(T t) throws java.lang.Throwable;

    }

    @FunctionalInterface
    public interface ThrowableFunction<T, R> extends Function<T, R> {

        @Override
        @SneakyThrows
        default R apply(T t) {
            return applyOrThrow(t);
        }

        R applyOrThrow(T t) throws java.lang.Throwable;

    }

    @FunctionalInterface
    public interface ThrowableSupplier<R> extends Supplier<R> {

        @Override
        @SneakyThrows
        default R get() {
            return getOrThrow();
        }

        R getOrThrow() throws java.lang.Throwable;

    }

    @FunctionalInterface
    public interface ThrowablePredicate<T> extends Predicate<T> {

        @Override
        @SneakyThrows
        default boolean test(T t) {
            return testOrThrow(t);
        }

        boolean testOrThrow(T t) throws java.lang.Throwable;
    }

    @FunctionalInterface
    public interface ThrowableBiConsumer<T1, T2> extends BiConsumer<T1, T2> {

        @Override
        @SneakyThrows
        default void accept(T1 t1, T2 t2) {
            acceptOrThrow(t1, t2);
        }

        void acceptOrThrow(T1 t1, T2 t2) throws java.lang.Throwable;

    }

    @FunctionalInterface
    public interface ThrowableBiFunction<T1, T2, R> extends BiFunction<T1, T2, R> {

        @Override
        @SneakyThrows
        default R apply(T1 t1, T2 t2) {
            return applyOrThrow(t1, t2);
        }

        R applyOrThrow(T1 t1, T2 t2) throws java.lang.Throwable;

    }

    @FunctionalInterface
    public interface ThrowableBiPredicate<T1, T2> extends BiPredicate<T1, T2> {

        @Override
        @SneakyThrows
        default boolean test(T1 t1, T2 t2) {
            return testOrThrow(t1, t2);
        }

        boolean testOrThrow(T1 t1, T2 t2) throws java.lang.Throwable;
    }

    /**
     * <p>Builds a consumer that can throw checked exceptions.</p>
     * <p>Example:
     * <pre>{@literal
     * Consumer<T> example() {
     *     return throwableConsumer(x -> {
     *         throw new Exception();
     *     });
     * }
     * }</pre>
     * </p>
     */
    public static <T> Consumer<T> throwableConsumer(ThrowableConsumer<T> consumer) {
        return consumer;
    }

    /**
     * <p>Builds a function that can throw checked exceptions.</p>
     * <p>Example:
     * <pre>{@literal
     * Function<T, R> example() {
     *     return throwableFunction(x -> {
     *         throw new Exception();
     *     });
     * }
     * }</pre>
     * </p>
     */
    public static <T, R> Function<T, R> throwableFunction(ThrowableFunction<T, R> function) {
        return function;
    }


    /**
     * <p>Builds a supplier that can throw checked exceptions.</p>
     * <p>Example:
     * <pre>{@literal
     * Supplier<R> example() {
     *     return throwableSupplier(() -> {
     *         throw new Exception();
     *     });
     * }
     * }</pre>
     * </p>
     */
    public static <R> Supplier<R> throwableSupplier(ThrowableSupplier<R> supplier) {
        return supplier;
    }

    /**
     * <p>Builds a predicate that can throw checked exceptions.</p>
     * <p>Example:
     * <pre>{@literal
     * Predicate<T> example() {
     *     return throwablePredicate(x -> {
     *         throw new Exception();
     *     });
     * }
     * }</pre>
     * </p>
     */
    public static <T> Predicate<T> throwablePredicate(ThrowablePredicate<T> predicate) {
        return predicate;
    }

    /**
     * <p>Builds a biconsumer that can throw checked exceptions.</p>
     * <p>Example:
     * <pre>{@literal
     * BiConsumer<T1, T2> example() {
     *     return throwableBiConsumer((x, y) -> {
     *         throw new Exception();
     *     });
     * }
     * }</pre>
     * </p>
     */
    public static <T1, T2> BiConsumer<T1, T2> throwableBiConsumer(ThrowableBiConsumer<T1, T2> consumer) {
        return consumer;
    }

    /**
     * <p>Builds a bifunction that can throw checked exceptions.</p>
     * <p>Example:
     * <pre>{@literal
     * BiFunction<T1, T2, R> example() {
     *     return throwableBiFunction((x, y) -> {
     *         throw new Exception();
     *     });
     * }
     * }</pre>
     * </p>
     */
    public static <T1, T2, R> BiFunction<T1, T2, R> throwableBiFunction(ThrowableBiFunction<T1, T2, R> function) {
        return function;
    }

    /**
     * <p>Builds a bipredicate that can throw checked exceptions.</p>
     * <p>Example:
     * <pre>{@literal
     * BiPredicate<T1, T2> example() {
     *     return throwableBiPredicate((x, y) -> {
     *         throw new Exception();
     *     });
     * }
     * }</pre>
     * </p>
     */
    public static <T1, T2> BiPredicate<T1, T2> throwableBiPredicate(ThrowableBiPredicate<T1, T2> predicate) {
        return predicate;
    }

}
