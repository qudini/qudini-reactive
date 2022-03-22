package com.qudini.reactive.utils;

import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toUnmodifiableList;
import static lombok.AccessLevel.PRIVATE;

/**
 * <p>Utilities around collectors.</p>
 */
@NoArgsConstructor(access = PRIVATE)
public final class MoreCollectors {

    /**
     * <p>Collects into a {@link Map}, mapping values with {@link Function#identity()}</p>
     */
    public static <T, K> Collector<T, ?, Map<K, T>> toMap(
            Function<? super T, ? extends K> keyMapper
    ) {
        return Collectors.toMap(keyMapper, identity());
    }

    /**
     * <p>Collects into an unmodifiable {@link Map}, mapping values with {@link Function#identity()}</p>
     */
    public static <T, K> Collector<T, ?, Map<K, T>> toUnmodifiableMap(
            Function<? super T, ? extends K> keyMapper
    ) {
        return Collectors.toUnmodifiableMap(keyMapper, identity());
    }

    /**
     * <p>Collects into a {@link LinkedHashMap}, mapping values with {@link Function#identity()}</p>
     */
    public static <T, K> Collector<T, ?, Map<K, T>> toLinkedMap(
            Function<? super T, ? extends K> keyMapper
    ) {
        return toLinkedMap(keyMapper, identity());
    }

    /**
     * <p>Collects into a {@link LinkedHashMap}.</p>
     */
    public static <T, K, U> Collector<T, ?, Map<K, U>> toLinkedMap(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper
    ) {
        return Collectors.toMap(
                keyMapper,
                valueMapper,
                MoreCollectors::throwingMerger,
                LinkedHashMap::new
        );
    }

    /**
     * <p>Collects into an unmodifiable {@link LinkedHashMap}, mapping values with {@link Function#identity()}</p>
     */
    public static <T, K> Collector<T, ?, Map<K, T>> toUnmodifiableLinkedMap(
            Function<? super T, ? extends K> keyMapper
    ) {
        return toUnmodifiableLinkedMap(keyMapper, identity());
    }

    /**
     * <p>Collects into an unmodifiable {@link LinkedHashMap}.</p>
     */
    public static <T, K, U> Collector<T, ?, Map<K, U>> toUnmodifiableLinkedMap(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper
    ) {
        return Collectors.collectingAndThen(
                toLinkedMap(keyMapper, valueMapper),
                Collections::unmodifiableMap
        );
    }

    /**
     * <p>Collects into a {@link TreeMap}, mapping values with {@link Function#identity()}</p>
     */
    public static <T, K> Collector<T, ?, Map<K, T>> toTreeMap(
            Function<? super T, ? extends K> keyMapper
    ) {
        return toTreeMap(keyMapper, identity());
    }

    /**
     * <p>Collects into a {@link TreeMap}.</p>
     */
    public static <T, K, U> Collector<T, ?, Map<K, U>> toTreeMap(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper
    ) {
        return Collectors.toMap(
                keyMapper,
                valueMapper,
                MoreCollectors::throwingMerger,
                TreeMap::new
        );
    }

    /**
     * <p>Collects into an unmodifiable {@link TreeMap}, mapping values with {@link Function#identity()}</p>
     */
    public static <T, K> Collector<T, ?, Map<K, T>> toUnmodifiableTreeMap(
            Function<? super T, ? extends K> keyMapper
    ) {
        return toUnmodifiableTreeMap(keyMapper, identity());
    }

    /**
     * <p>Collects into an unmodifiable {@link TreeMap}.</p>
     */
    public static <T, K, U> Collector<T, ?, Map<K, U>> toUnmodifiableTreeMap(
            Function<? super T, ? extends K> keyMapper,
            Function<? super T, ? extends U> valueMapper
    ) {
        return Collectors.collectingAndThen(
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
        return Collectors.collectingAndThen(
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
        return Collectors.collectingAndThen(
                toTreeSet(),
                Collections::unmodifiableSet
        );
    }

    /**
     * <p>Returns an unmodifiable map containing unmodifiable lists.</p>
     */
    public static <T, K> Collector<T, ?, Map<K, List<T>>> groupingByUnmodifiable(
            Function<? super T, ? extends K> classifier
    ) {
        return groupingByUnmodifiable(classifier, HashMap::new, toUnmodifiableList());
    }

    /**
     * <p>Returns an unmodifiable map.</p>
     */
    public static <T, K, A, D> Collector<T, ?, Map<K, D>> groupingByUnmodifiable(
            Function<? super T, ? extends K> classifier,
            Collector<? super T, A, D> collector
    ) {
        return groupingByUnmodifiable(classifier, HashMap::new, collector);
    }

    /**
     * <p>Returns an unmodifiable map containing unmodifiable lists.</p>
     */
    public static <T, K, M extends Map<K, List<T>>> Collector<T, ?, Map<K, List<T>>> groupingByUnmodifiable(
            Function<? super T, ? extends K> classifier,
            Supplier<M> supplier
    ) {
        return groupingByUnmodifiable(classifier, supplier, toUnmodifiableList());
    }

    /**
     * <p>Returns an unmodifiable map.</p>
     */
    public static <T, K, D, A, M extends Map<K, D>> Collector<T, ?, Map<K, D>> groupingByUnmodifiable(
            Function<? super T, ? extends K> classifier,
            Supplier<M> supplier,
            Collector<? super T, A, D> collector
    ) {
        return Collectors.collectingAndThen(
                Collectors.groupingBy(classifier, supplier, collector),
                Collections::unmodifiableMap
        );
    }

    public static <T> T throwingMerger(T x, T y) {
        throw new IllegalStateException("Unable to merge " + x + " and " + y);
    }

}
