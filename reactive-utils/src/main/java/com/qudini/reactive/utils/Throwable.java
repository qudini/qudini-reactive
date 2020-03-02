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
public final class Throwable {

    @FunctionalInterface
    public interface ThrowableConsumer<I> extends Consumer<I> {

        @Override
        @SneakyThrows
        default void accept(I i) {
            acceptOrThrow(i);
        }

        void acceptOrThrow(I i) throws java.lang.Throwable;

    }

    @FunctionalInterface
    public interface ThrowableFunction<I, O> extends Function<I, O> {

        @Override
        @SneakyThrows
        default O apply(I i) {
            return applyOrThrow(i);
        }

        O applyOrThrow(I i) throws java.lang.Throwable;

    }

    @FunctionalInterface
    public interface ThrowableSupplier<O> extends Supplier<O> {

        @Override
        @SneakyThrows
        default O get() {
            return getOrThrow();
        }

        O getOrThrow() throws java.lang.Throwable;

    }

    @FunctionalInterface
    public interface ThrowablePredicate<I> extends Predicate<I> {

        @Override
        @SneakyThrows
        default boolean test(I i) {
            return testOrThrow(i);
        }

        boolean testOrThrow(I i) throws java.lang.Throwable;
    }

    @FunctionalInterface
    public interface ThrowableBiConsumer<I, J> extends BiConsumer<I, J> {

        @Override
        @SneakyThrows
        default void accept(I i, J j) {
            acceptOrThrow(i, j);
        }

        void acceptOrThrow(I i, J j) throws java.lang.Throwable;

    }

    @FunctionalInterface
    public interface ThrowableBiFunction<I, J, O> extends BiFunction<I, J, O> {

        @Override
        @SneakyThrows
        default O apply(I i, J j) {
            return applyOrThrow(i, j);
        }

        O applyOrThrow(I i, J j) throws java.lang.Throwable;

    }

    @FunctionalInterface
    public interface ThrowableBiPredicate<I, J> extends BiPredicate<I, J> {

        @Override
        @SneakyThrows
        default boolean test(I i, J j) {
            return testOrThrow(i, j);
        }

        boolean testOrThrow(I i, J j) throws java.lang.Throwable;
    }

    public static <I> Consumer<I> consumer(ThrowableConsumer<I> consumer) {
        return consumer;
    }

    public static <I, O> Function<I, O> function(ThrowableFunction<I, O> function) {
        return function;
    }

    public static <O> Supplier<O> supplier(ThrowableSupplier<O> supplier) {
        return supplier;
    }

    public static <I> Predicate<I> predicate(ThrowablePredicate<I> predicate) {
        return predicate;
    }

    public static <I, J> BiConsumer<I, J> biConsumer(ThrowableBiConsumer<I, J> consumer) {
        return consumer;
    }

    public static <I, J, O> BiFunction<I, J, O> biFunction(ThrowableBiFunction<I, J, O> function) {
        return function;
    }

    public static <I, J> BiPredicate<I, J> biPredicate(ThrowableBiPredicate<I, J> predicate) {
        return predicate;
    }

}
