package com.qudini.reactive.utils;

import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class MoreTuples {

    public static <T1, T2> Tuple2<T1, T2> fromEntry(Map.Entry<T1, T2> entry) {
        return Tuples.of(entry.getKey(), entry.getValue());
    }

    public static <T> Tuple2<T, T> fromArray(T[] array) {
        return Tuples.of(array[0], array[1]);
    }

    public static <T1, T2> Function<Tuple2<T1, T2>, T1> left() {
        return Tuple2::getT1;
    }

    public static <T1, T2> Function<Tuple2<T1, T2>, T2> right() {
        return Tuple2::getT2;
    }

    public static <T, R> Function<Tuple2<T, T>, Tuple2<R, R>> each(Function<T, R> mapper) {
        return tuple -> Tuples.of(mapper.apply(tuple.getT1()), mapper.apply(tuple.getT2()));
    }

    public static <T> Predicate<Tuple2<T, T>> ifEach(Predicate<T> predicate) {
        return tuple -> predicate.test(tuple.getT1()) && predicate.test(tuple.getT2());
    }

    public static <T> Predicate<Tuple2<T, T>> ifEither(Predicate<T> predicate) {
        return tuple -> predicate.test(tuple.getT1()) || predicate.test(tuple.getT2());
    }

    public static <T1, T2, R> Function<Tuple2<T1, T2>, R> both(BiFunction<T1, T2, R> mapper) {
        return tuple -> mapper.apply(tuple.getT1(), tuple.getT2());
    }

    public static <T1, T2> Consumer<Tuple2<T1, T2>> takeBoth(BiConsumer<T1, T2> consumer) {
        return tuple -> consumer.accept(tuple.getT1(), tuple.getT2());
    }

    public static <T1, T2> Predicate<Tuple2<T1, T2>> ifBoth(BiPredicate<T1, T2> predicate) {
        return tuple -> predicate.test(tuple.getT1(), tuple.getT2());
    }

    public static <T1, T2, R> Function<Tuple2<T1, T2>, Tuple2<R, T2>> left(Function<T1, R> mapper) {
        return tuple -> Tuples.of(mapper.apply(tuple.getT1()), tuple.getT2());
    }

    public static <T1, T2, R> Function<Tuple2<T1, T2>, Mono<Tuple2<R, T2>>> leftWhen(Function<T1, Mono<R>> mapper) {
        return tuple -> Mono.zip(mapper.apply(tuple.getT1()), Mono.just(tuple.getT2()));
    }

    public static <T1, T2> Predicate<Tuple2<T1, T2>> ifLeft(Predicate<T1> predicate) {
        return tuple -> predicate.test(tuple.getT1());
    }

    public static <T1, T2, R> Function<Tuple2<T1, T2>, Tuple2<T1, R>> right(Function<T2, R> mapper) {
        return tuple -> Tuples.of(tuple.getT1(), mapper.apply(tuple.getT2()));
    }

    public static <T1, T2, R> Function<Tuple2<T1, T2>, Mono<Tuple2<T1, R>>> rightWhen(Function<T2, Mono<R>> mapper) {
        return tuple -> Mono.zip(Mono.just(tuple.getT1()), mapper.apply(tuple.getT2()));
    }

    public static <T1, T2> Predicate<Tuple2<T1, T2>> ifRight(Predicate<T2> predicate) {
        return tuple -> predicate.test(tuple.getT2());
    }

}
