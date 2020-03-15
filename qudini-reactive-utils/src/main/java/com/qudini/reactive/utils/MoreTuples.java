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
 * <p>Utilities around tuples.</p>
 */
@NoArgsConstructor(access = PRIVATE)
public final class MoreTuples {

    /**
     * <p>Builds a tuple given a map entry.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Flux<Tuple2<T1, T2>> example(Mono<Map<T1, T2>> map) {
     *     return map
     *             .flatMapIterable(Map::entrySet)
     *             .map(MoreTuples::fromEntry);
     * }
     * }</pre>
     */
    public static <T1, T2> Tuple2<T1, T2> fromEntry(Map.Entry<T1, T2> entry) {
        return Tuples.of(entry.getKey(), entry.getValue());
    }

    /**
     * <p>Builds a tuple given a 2-element array.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Tuple2<Integer, Integer> example() {
     *     return MoreTuples.fromArray(new Integer[]{1, 2});
     * }
     * }</pre>
     */
    public static <T> Tuple2<T, T> fromArray(T[] array) {
        return Tuples.of(array[0], array[1]);
    }

    /**
     * <p>Aliases {@link Tuple2#getT1()}.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Mono<Integer> example(Mono<Tuple2<Integer, String>> mono) {
     *     return mono.map(MoreTuples::left);
     * }
     * }</pre>
     */
    public static <T1, T2> T1 left(Tuple2<T1, T2> tuple) {
        return tuple.getT1();
    }

    /**
     * <p>Aliases {@link Tuple2#getT2()}.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Mono<String> example(Mono<Tuple2<Integer, String>> mono) {
     *     return mono.map(MoreTuples::right);
     * }
     * }</pre>
     */
    public static <T1, T2> T2 right(Tuple2<T1, T2> tuple) {
        return tuple.getT2();
    }

    /**
     * <p>Applies the same mapper on each element of a tuple, returning a new tuple.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Mono<Tuple2<String, String>> example(Mono<Tuple2<Integer, Integer>> mono) {
     *     return mono.map(onEach(Object::toString));
     * }
     * }</pre>
     */
    public static <T, R> Function<Tuple2<T, T>, Tuple2<R, R>> onEach(Function<T, R> mapper) {
        return tuple -> Tuples.of(mapper.apply(tuple.getT1()), mapper.apply(tuple.getT2()));
    }

    /**
     * <p>Tests a predicate against each element of a tuple,
     * and returns a new predicate that will return {@code true}
     * if both returned {@code true}.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Mono<Tuple2<Integer, Integer>> example(Mono<Tuple2<Integer, Integer>> mono) {
     *     return mono.filter(ifEach(i -> 0 < i));
     * }
     * }</pre>
     */
    public static <T> Predicate<Tuple2<T, T>> ifEach(Predicate<T> predicate) {
        return tuple -> predicate.test(tuple.getT1()) && predicate.test(tuple.getT2());
    }

    /**
     * <p>Tests a predicate against each element of a tuple,
     * and returns a new predicate that will return {@code true}
     * if at least one returned {@code true}.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Mono<Tuple2<Integer, Integer>> example(Mono<Tuple2<Integer, Integer>> mono) {
     *     return mono.filter(ifEither(i -> 0 < i));
     * }
     * }</pre>
     */
    public static <T> Predicate<Tuple2<T, T>> ifEither(Predicate<T> predicate) {
        return tuple -> predicate.test(tuple.getT1()) || predicate.test(tuple.getT2());
    }

    /**
     * <p>Reduces a tuple via a bifunction.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Mono<String> example(Mono<Tuple2<Integer, String>> mono) {
     *     return mono.map(onBoth((i, s) -> s + i));
     * }
     * }</pre>
     * <p>Example:</p>
     * <pre>{@literal
     * Mono<String> example(Mono<Tuple2<Integer, String>> mono) {
     *     return mono.flatMap(onBoth((i, s) -> Mono.just(s + i)));
     * }
     * }</pre>
     * <p>Example:</p>
     * <pre>{@literal
     * Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
     *     return mono.filterWhen(onBoth((i, s) -> Mono.just("foo42".equals(s + i))));
     * }
     * }</pre>
     */
    public static <T1, T2, R> Function<Tuple2<T1, T2>, R> onBoth(BiFunction<T1, T2, R> mapper) {
        return tuple -> mapper.apply(tuple.getT1(), tuple.getT2());
    }

    /**
     * <p>Consumes the left value of a tuple.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
     *     return mono.doOnNext(takeLeft(i -> log.debug("i:{}", i)));
     * }
     * }</pre>
     */
    public static <T1, T2> Consumer<Tuple2<T1, T2>> takeLeft(Consumer<T1> consumer) {
        return tuple -> consumer.accept(tuple.getT1());
    }

    /**
     * <p>Consumes the right value of a tuple.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
     *     return mono.doOnNext(takeRight(s -> log.debug("s:{}", s)));
     * }
     * }</pre>
     */
    public static <T1, T2> Consumer<Tuple2<T1, T2>> takeRight(Consumer<T2> consumer) {
        return tuple -> consumer.accept(tuple.getT2());
    }

    /**
     * <p>Consumes a tuple via a biconsumer.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
     *     return mono.doOnNext(takeBoth((i, s) -> log.debug("i:{} s:{}", i, s)));
     * }
     * }</pre>
     */
    public static <T1, T2> Consumer<Tuple2<T1, T2>> takeBoth(BiConsumer<T1, T2> consumer) {
        return tuple -> consumer.accept(tuple.getT1(), tuple.getT2());
    }

    /**
     * <p>Applies a bipredicate against a tuple.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
     *     return mono.filter(ifBoth((i, s) -> "foo42".equals(s + i)));
     * }
     * }</pre>
     */
    public static <T1, T2> Predicate<Tuple2<T1, T2>> ifBoth(BiPredicate<T1, T2> predicate) {
        return tuple -> predicate.test(tuple.getT1(), tuple.getT2());
    }

    /**
     * <p>Applies a mapper on the left value of a tuple,
     * and returns a new tuple with the right value unchanged.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
     *     return mono.map(onLeft(i -> i + 1));
     * }
     * }</pre>
     */
    public static <T1, T2, R> Function<Tuple2<T1, T2>, Tuple2<R, T2>> onLeft(Function<T1, R> mapper) {
        return tuple -> Tuples.of(mapper.apply(tuple.getT1()), tuple.getT2());
    }

    /**
     * <p>Applies a mapper on the left value of a tuple,
     * and returns a new tuple inside a mono with the right value unchanged.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
     *     return mono.flatMap(onLeftWhen(i -> Mono.just(i + 1)));
     * }
     * }</pre>
     */
    public static <T1, T2, R> Function<Tuple2<T1, T2>, Mono<Tuple2<R, T2>>> onLeftWhen(Function<T1, Mono<R>> mapper) {
        return tuple -> Mono.zip(mapper.apply(tuple.getT1()), Mono.just(tuple.getT2()));
    }

    /**
     * <p>Reduces a tuple by mapping its left value, losing its right value.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
     *     return mono.filterWhen(fromLeft(i -> Mono.just(0 < i)));
     * }
     * }</pre>
     */
    public static <T1, T2, R> Function<Tuple2<T1, T2>, R> fromLeft(Function<T1, R> mapper) {
        return tuple -> mapper.apply(tuple.getT1());
    }

    /**
     * <p>Applies a predicate on the left value of a tuple.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
     *     return mono.filter(ifLeft(i -> 0 < i));
     * }
     * }</pre>
     */
    public static <T1, T2> Predicate<Tuple2<T1, T2>> ifLeft(Predicate<T1> predicate) {
        return tuple -> predicate.test(tuple.getT1());
    }

    /**
     * <p>Applies a mapper on the right value of a tuple,
     * and returns a new tuple with the left value unchanged.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
     *     return mono.map(onRight(s -> s + "bar"));
     * }
     * }</pre>
     */
    public static <T1, T2, R> Function<Tuple2<T1, T2>, Tuple2<T1, R>> onRight(Function<T2, R> mapper) {
        return tuple -> Tuples.of(tuple.getT1(), mapper.apply(tuple.getT2()));
    }

    /**
     * <p>Applies a mapper on the right value of a tuple,
     * and returns a new tuple inside a mono with the left value unchanged.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
     *     return mono.flatMap(onRightWhen(s -> Mono.just(s + "bar")));
     * }
     * }</pre>
     */
    public static <T1, T2, R> Function<Tuple2<T1, T2>, Mono<Tuple2<T1, R>>> onRightWhen(Function<T2, Mono<R>> mapper) {
        return tuple -> Mono.zip(Mono.just(tuple.getT1()), mapper.apply(tuple.getT2()));
    }

    /**
     * <p>Reduces a tuple by mapping its right value, losing its left value.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
     *     return mono.filterWhen(fromRight(s -> Mono.just("foobar".equals(s))));
     * }
     * }</pre>
     */
    public static <T1, T2, R> Function<Tuple2<T1, T2>, R> fromRight(Function<T2, R> mapper) {
        return tuple -> mapper.apply(tuple.getT2());
    }

    /**
     * <p>Applies a predicate on the right value of a tuple.</p>
     * <p>Example:</p>
     * <pre>{@literal
     * Mono<Tuple2<Integer, String>> example(Mono<Tuple2<Integer, String>> mono) {
     *     return mono.filter(ifRight("foobar"::equals));
     * }
     * }</pre>
     */
    public static <T1, T2> Predicate<Tuple2<T1, T2>> ifRight(Predicate<T2> predicate) {
        return tuple -> predicate.test(tuple.getT2());
    }

}
