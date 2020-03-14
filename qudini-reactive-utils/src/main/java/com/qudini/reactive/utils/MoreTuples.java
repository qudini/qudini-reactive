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

/**
 * Utilities around tuples.
 */
@NoArgsConstructor(access = PRIVATE)
public final class MoreTuples {

    /**
     * <p>Builds a tuple given a map entry.</p>
     * <p>Example:
     * <pre>{@literal
     * Flux<Tuple2<T1, T2>> example(Mono<Map<T1, T2>> map) {
     *     return map
     *             .flatMapIterable(Map::entrySet)
     *             .map(MoreTuples::fromEntry);
     * }
     * }</pre>
     * </p>
     */
    public static <T1, T2> Tuple2<T1, T2> fromEntry(Map.Entry<T1, T2> entry) {
        return Tuples.of(entry.getKey(), entry.getValue());
    }

    /**
     * <p>Builds a tuple given a 2-element array.</p>
     * <p>Example:
     * <pre>{@literal
     * Tuple2<Integer, Integer> example() {
     *     return MoreTuples.fromArray(new Integer[]{1, 2});
     * }
     * }</pre>
     * </p>
     */
    public static <T> Tuple2<T, T> fromArray(T[] array) {
        return Tuples.of(array[0], array[1]);
    }

    /**
     * <p>Applies the same mapper on each element of a tuple, returning a new tuple.</p>
     * <p>Example:
     * <pre>{@literal
     * Mono<Tuple2<String, String>> example(Mono<Tuple2<Integer, Integer>> mono) {
     *     return mono.map(each(Object::toString));
     * }
     * }</pre>
     * </p>
     */
    public static <T, R> Function<Tuple2<T, T>, Tuple2<R, R>> each(Function<T, R> mapper) {
        return tuple -> Tuples.of(mapper.apply(tuple.getT1()), mapper.apply(tuple.getT2()));
    }

    /**
     * <p>Tests a predicate against each element of a tuple,
     * and returns a new predicate that will return {@code true}
     * if both returned {@code true}.</p>
     * <p>Example:
     * <pre>{@literal
     * Mono<Tuple2<Integer, Integer>> example(Mono<Tuple2<Integer, Integer>> mono) {
     *     return mono.filter(ifEach(i -> 0 < i));
     * }
     * }</pre>
     * </p>
     */
    public static <T> Predicate<Tuple2<T, T>> ifEach(Predicate<T> predicate) {
        return tuple -> predicate.test(tuple.getT1()) && predicate.test(tuple.getT2());
    }

    /**
     * <p>Tests a predicate against each element of a tuple,
     * and returns a new predicate that will return {@code true}
     * if at least one returned {@code true}.</p>
     * <p>Example:
     * <pre>{@literal
     * Mono<Tuple2<Integer, Integer>> example(Mono<Tuple2<Integer, Integer>> mono) {
     *     return mono.filter(ifEither(i -> 0 < i));
     * }
     * }</pre>
     * </p>
     */
    public static <T> Predicate<Tuple2<T, T>> ifEither(Predicate<T> predicate) {
        return tuple -> predicate.test(tuple.getT1()) || predicate.test(tuple.getT2());
    }

    /**
     * <p>Reduces a tuple via a bifunction.</p>
     * <p>Example:
     * <pre>{@literal
     * Mono<String> example(Mono<Tuple2<Integer, String>> mono) {
     *     return mono.map(both((i, s) -> s + i));
     * }
     * }</pre>
     * </p>
     * <p>Example:
     * <pre>{@literal
     * Mono<String> example(Mono<Tuple2<Integer, String>> mono) {
     *     return mono.flatMap(both((i, s) -> Mono.just(s + i)));
     * }
     * }</pre>
     * </p>
     * <p>Example:
     * <pre>{@literal
     * Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
     *     return mono.filterWhen(both((i, s) -> Mono.just("foo42".equals(s + i))));
     * }
     * }</pre>
     * </p>
     */
    public static <T1, T2, R> Function<Tuple2<T1, T2>, R> both(BiFunction<T1, T2, R> mapper) {
        return tuple -> mapper.apply(tuple.getT1(), tuple.getT2());
    }

    /**
     * <p>Consumes a tuple via a biconsumer.</p>
     * <p>Example:
     * <pre>{@literal
     * Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
     *     return mono.doOnNext(takeBoth((i, s) -> log.debug("i:{} s:{}", i, s)))
     * }
     * }</pre>
     * </p>
     */
    public static <T1, T2> Consumer<Tuple2<T1, T2>> takeBoth(BiConsumer<T1, T2> consumer) {
        return tuple -> consumer.accept(tuple.getT1(), tuple.getT2());
    }

    /**
     * <p>Applies a bipredicate against a tuple.</p>
     * <p>Example:
     * <pre>{@literal
     * Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
     *     return mono.filter(ifBoth((i, s) -> "foo42".equals(s + i)));
     * }
     * }</pre>
     * </p>
     */
    public static <T1, T2> Predicate<Tuple2<T1, T2>> ifBoth(BiPredicate<T1, T2> predicate) {
        return tuple -> predicate.test(tuple.getT1(), tuple.getT2());
    }

    /**
     * <p>Applies a mapper on the left value of a tuple,
     * and returns a new tuple with the right value unchanged.</p>
     * <p>Example:
     * <pre>{@literal
     * Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
     *     return mono.map(left(i -> i + 1));
     * }
     * }</pre>
     * </p>
     */
    public static <T1, T2, R> Function<Tuple2<T1, T2>, Tuple2<R, T2>> left(Function<T1, R> mapper) {
        return tuple -> Tuples.of(mapper.apply(tuple.getT1()), tuple.getT2());
    }

    /**
     * <p>Applies a mapper on the left value of a tuple,
     * and returns a new tuple inside a mono with the right value unchanged.</p>
     * <p>Example:
     * <pre>{@literal
     * Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
     *     return mono.flatMap(leftWhen(i -> Mono.just(i + 1)));
     * }
     * }</pre>
     * </p>
     */
    public static <T1, T2, R> Function<Tuple2<T1, T2>, Mono<Tuple2<R, T2>>> leftWhen(Function<T1, Mono<R>> mapper) {
        return tuple -> Mono.zip(mapper.apply(tuple.getT1()), Mono.just(tuple.getT2()));
    }

    /**
     * <p>Applies a predicate on the left value of a tuple.</p>
     * <p>Example:
     * <pre>{@literal
     * Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
     *     return mono.filter(ifLeft(i -> 0 < i));
     * }
     * }</pre>
     * </p>
     */
    public static <T1, T2> Predicate<Tuple2<T1, T2>> ifLeft(Predicate<T1> predicate) {
        return tuple -> predicate.test(tuple.getT1());
    }

    /**
     * <p>Applies a mapper on the right value of a tuple,
     * and returns a new tuple with the left value unchanged.</p>
     * <p>Example:
     * <pre>{@literal
     * Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
     *     return mono.map(right(s -> s + "bar"));
     * }
     * }</pre>
     * </p>
     */
    public static <T1, T2, R> Function<Tuple2<T1, T2>, Tuple2<T1, R>> right(Function<T2, R> mapper) {
        return tuple -> Tuples.of(tuple.getT1(), mapper.apply(tuple.getT2()));
    }

    /**
     * <p>Applies a mapper on the right value of a tuple,
     * and returns a new tuple inside a mono with the left value unchanged.</p>
     * <p>Example:
     * <pre>{@literal
     * Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
     *     return mono.flatMap(rightWhen(s -> Mono.just(s + "bar")));
     * }
     * }</pre>
     * </p>
     */
    public static <T1, T2, R> Function<Tuple2<T1, T2>, Mono<Tuple2<T1, R>>> rightWhen(Function<T2, Mono<R>> mapper) {
        return tuple -> Mono.zip(Mono.just(tuple.getT1()), mapper.apply(tuple.getT2()));
    }

    /**
     * <p>Applies a predicate on the right value of a tuple.</p>
     * <p>Example:
     * <pre>{@literal
     * Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
     *     return mono.filter(ifRight("foobar"::equals));
     * }
     * }</pre>
     * </p>
     */
    public static <T1, T2> Predicate<Tuple2<T1, T2>> ifRight(Predicate<T2> predicate) {
        return tuple -> predicate.test(tuple.getT2());
    }

}
