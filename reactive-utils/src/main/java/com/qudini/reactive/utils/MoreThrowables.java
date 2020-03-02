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

    public static <T> Consumer<T> consumer(ThrowableConsumer<T> consumer) {
        return consumer;
    }

    public static <T, R> Function<T, R> function(ThrowableFunction<T, R> function) {
        return function;
    }

    public static <R> Supplier<R> supplier(ThrowableSupplier<R> supplier) {
        return supplier;
    }

    public static <T> Predicate<T> predicate(ThrowablePredicate<T> predicate) {
        return predicate;
    }

    public static <T1, T2> BiConsumer<T1, T2> biConsumer(ThrowableBiConsumer<T1, T2> consumer) {
        return consumer;
    }

    public static <T1, T2, R> BiFunction<T1, T2, R> biFunction(ThrowableBiFunction<T1, T2, R> function) {
        return function;
    }

    public static <T1, T2> BiPredicate<T1, T2> biPredicate(ThrowableBiPredicate<T1, T2> predicate) {
        return predicate;
    }

}
