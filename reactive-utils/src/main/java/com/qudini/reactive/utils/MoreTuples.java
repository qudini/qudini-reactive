package com.qudini.reactive.utils;

import lombok.NoArgsConstructor;
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

    public static <I, J> Tuple2<I, J> fromEntry(Map.Entry<I, J> entry) {
        return Tuples.of(entry.getKey(), entry.getValue());
    }

    public static <I> Tuple2<I, I> fromArray(I[] array) {
        return Tuples.of(array[0], array[1]);
    }

    public static <I, J> Function<Tuple2<I, J>, I> left() {
        return Tuple2::getT1;
    }

    public static <I, J> Function<Tuple2<I, J>, J> right() {
        return Tuple2::getT2;
    }

    public static <I, O> Function<Tuple2<I, I>, Tuple2<O, O>> each(Function<I, O> mapper) {
        return tuple -> Tuples.of(mapper.apply(tuple.getT1()), mapper.apply(tuple.getT2()));
    }

    public static <I, J, O> Function<Tuple2<I, J>, O> both(BiFunction<I, J, O> mapper) {
        return tuple -> mapper.apply(tuple.getT1(), tuple.getT2());
    }

    public static <I, J> Consumer<Tuple2<I, J>> takeBoth(BiConsumer<I, J> consumer) {
        return tuple -> consumer.accept(tuple.getT1(), tuple.getT2());
    }

    public static <I, J> Predicate<Tuple2<I, J>> ifBoth(BiPredicate<I, J> predicate) {
        return tuple -> predicate.test(tuple.getT1(), tuple.getT2());
    }

    public static <I, J, O> Function<Tuple2<I, J>, Tuple2<O, J>> left(Function<I, O> mapper) {
        return tuple -> Tuples.of(mapper.apply(tuple.getT1()), tuple.getT2());
    }

    public static <I, J> Predicate<Tuple2<I, J>> ifLeft(Predicate<I> predicate) {
        return tuple -> predicate.test(tuple.getT1());
    }

    public static <I, J, O> Function<Tuple2<I, J>, Tuple2<I, O>> right(Function<J, O> mapper) {
        return tuple -> Tuples.of(tuple.getT1(), mapper.apply(tuple.getT2()));
    }

    public static <I, J> Predicate<Tuple2<I, J>> ifRight(Predicate<J> predicate) {
        return tuple -> predicate.test(tuple.getT2());
    }

}
