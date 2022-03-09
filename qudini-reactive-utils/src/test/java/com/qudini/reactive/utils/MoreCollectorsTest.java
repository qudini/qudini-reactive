package com.qudini.reactive.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static com.qudini.reactive.utils.MoreCollectors.toLinkedMap;
import static com.qudini.reactive.utils.MoreCollectors.toLinkedSet;
import static com.qudini.reactive.utils.MoreCollectors.toTreeMap;
import static com.qudini.reactive.utils.MoreCollectors.toTreeSet;
import static com.qudini.reactive.utils.MoreCollectors.toUnmodifiableLinkedMap;
import static com.qudini.reactive.utils.MoreCollectors.toUnmodifiableLinkedSet;
import static com.qudini.reactive.utils.MoreCollectors.toUnmodifiableTreeMap;
import static com.qudini.reactive.utils.MoreCollectors.toUnmodifiableTreeSet;
import static java.util.Map.entry;
import static java.util.function.Function.identity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("MoreCollectors")
class MoreCollectorsTest {

    @Test
    @DisplayName("should allow collecting into a LinkedHashMap")
    void linkedMap() {
        var map = Stream
                .of("aaa", "c", "bb")
                .collect(toLinkedMap(String::length, identity()));
        assertThat(map).containsExactly(
                entry(3, "aaa"),
                entry(1, "c"),
                entry(2, "bb")
        );
        map.put(4, "pass");
    }

    @Test
    @DisplayName("should allow collecting into an unmodifiable LinkedHashMap")
    void unmodifiableLinkedMap() {
        var map = Stream
                .of("aaa", "c", "bb")
                .collect(toUnmodifiableLinkedMap(String::length, identity()));
        assertThat(map).containsExactly(
                entry(3, "aaa"),
                entry(1, "c"),
                entry(2, "bb")
        );
        assertThrows(UnsupportedOperationException.class, () -> map.put(4, "fail"));
    }

    @Test
    @DisplayName("should allow collecting into a TreeMap")
    void treeMap() {
        var map = Stream
                .of("aaa", "c", "bb")
                .collect(toTreeMap(String::length, identity()));
        assertThat(map).containsExactly(
                entry(1, "c"),
                entry(2, "bb"),
                entry(3, "aaa")
        );
        map.put(4, "pass");
    }

    @Test
    @DisplayName("should allow collecting into an unmodifiable TreeMap")
    void unmodifiableTreeMap() {
        var map = Stream
                .of("aaa", "c", "bb")
                .collect(toUnmodifiableTreeMap(String::length, identity()));
        assertThat(map).containsExactly(
                entry(1, "c"),
                entry(2, "bb"),
                entry(3, "aaa")
        );
        assertThrows(UnsupportedOperationException.class, () -> map.put(4, "fail"));
    }

    @Test
    @DisplayName("should allow collecting into a LinkedHashSet")
    void linkedSet() {
        var set = Stream
                .of("aaa", "c", "bb")
                .collect(toLinkedSet());
        assertThat(set).containsExactly("aaa", "c", "bb");
        set.add("pass");
    }

    @Test
    @DisplayName("should allow collecting into an unmodifiable LinkedHashSet")
    void unmodifiableLinkedSet() {
        var set = Stream
                .of("aaa", "c", "bb")
                .collect(toUnmodifiableLinkedSet());
        assertThat(set).containsExactly("aaa", "c", "bb");
        assertThrows(UnsupportedOperationException.class, () -> set.add("fail"));
    }

    @Test
    @DisplayName("should allow collecting into a TreeSet")
    void treeSet() {
        var set = Stream
                .of("aaa", "c", "bb")
                .collect(toTreeSet());
        assertThat(set).containsExactly("aaa", "bb", "c");
        set.add("pass");
    }

    @Test
    @DisplayName("should allow collecting into an unmodifiable TreeSet")
    void unmodifiableTreeSet() {
        var set = Stream
                .of("aaa", "c", "bb")
                .collect(toUnmodifiableTreeSet());
        assertThat(set).containsExactly("aaa", "bb", "c");
        assertThrows(UnsupportedOperationException.class, () -> set.add("fail"));
    }

}
