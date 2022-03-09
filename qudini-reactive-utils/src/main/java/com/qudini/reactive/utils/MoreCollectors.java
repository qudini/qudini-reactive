package com.qudini.reactive.utils;

import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collector;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;
import static lombok.AccessLevel.PRIVATE;

/**
 * <p>Utilities around collectors.</p>
 */
@NoArgsConstructor(access = PRIVATE)
public final class MoreCollectors {

    /**
     * <p>Collects into a {@link LinkedHashMap}.</p>
     */
    public static <T, K, U> Collector<T, ?, Map<K, U>> toLinkedMap(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper
    ) {
        return toMap(
                keyMapper,
                valueMapper,
                MoreCollectors::throwingMerger,
                LinkedHashMap::new
        );
    }

    /**
     * <p>Collects into an unmodifiable {@link LinkedHashMap}.</p>
     */
    public static <T, K, U> Collector<T, ?, Map<K, U>> toUnmodifiableLinkedMap(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper
    ) {
        return collectingAndThen(
                toLinkedMap(keyMapper, valueMapper),
                Collections::unmodifiableMap
        );
    }

    /**
     * <p>Collects into a {@link TreeMap}.</p>
     */
    public static <T, K, U> Collector<T, ?, Map<K, U>> toTreeMap(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper
    ) {
        return toMap(
                keyMapper,
                valueMapper,
                MoreCollectors::throwingMerger,
                TreeMap::new
        );
    }

    /**
     * <p>Collects into an unmodifiable {@link TreeMap}.</p>
     */
    public static <T, K, U> Collector<T, ?, Map<K, U>> toUnmodifiableTreeMap(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper
    ) {
        return collectingAndThen(
                toTreeMap(keyMapper, valueMapper),
                Collections::unmodifiableMap
        );
    }

    /**
     * <p>Collects into a {@link LinkedHashSet}.</p>
     */
    public static <T> Collector<T, ?, Set<T>> toLinkedSet() {
        return toCollection(LinkedHashSet::new);
    }

    /**
     * <p>Collects into an unmodifiable {@link LinkedHashSet}.</p>
     */
    public static <T> Collector<T, ?, Set<T>> toUnmodifiableLinkedSet() {
        return collectingAndThen(
                toLinkedSet(),
                Collections::unmodifiableSet
        );
    }

    /**
     * <p>Collects into a {@link TreeSet}.</p>
     */
    public static <T> Collector<T, ?, Set<T>> toTreeSet() {
        return toCollection(TreeSet::new);
    }

    /**
     * <p>Collects into an unmodifiable {@link TreeSet}.</p>
     */
    public static <T> Collector<T, ?, Set<T>> toUnmodifiableTreeSet() {
        return collectingAndThen(
                toTreeSet(),
                Collections::unmodifiableSet
        );
    }

    private static <T> T throwingMerger(T x, T y) {
        throw new IllegalStateException("Unable to merge " + x + " and " + y + ", use Collectors.toMap(...) instead");
    }

}
