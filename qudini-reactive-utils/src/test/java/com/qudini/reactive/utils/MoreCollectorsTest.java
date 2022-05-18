package com.qudini.reactive.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;

import static com.qudini.reactive.utils.MoreCollectors.groupingByUnmodifiable;
import static com.qudini.reactive.utils.MoreCollectors.partitioningByUnmodifiable;
import static com.qudini.reactive.utils.MoreCollectors.toIdentityLinkedMap;
import static com.qudini.reactive.utils.MoreCollectors.toIdentityTreeMap;
import static com.qudini.reactive.utils.MoreCollectors.toLinkedMap;
import static com.qudini.reactive.utils.MoreCollectors.toLinkedSet;
import static com.qudini.reactive.utils.MoreCollectors.toIdentityMap;
import static com.qudini.reactive.utils.MoreCollectors.toTreeMap;
import static com.qudini.reactive.utils.MoreCollectors.toTreeSet;
import static com.qudini.reactive.utils.MoreCollectors.toUnmodifiableIdentityLinkedMap;
import static com.qudini.reactive.utils.MoreCollectors.toUnmodifiableIdentityTreeMap;
import static com.qudini.reactive.utils.MoreCollectors.toUnmodifiableLinkedMap;
import static com.qudini.reactive.utils.MoreCollectors.toUnmodifiableLinkedSet;
import static com.qudini.reactive.utils.MoreCollectors.toUnmodifiableIdentityMap;
import static com.qudini.reactive.utils.MoreCollectors.toUnmodifiableTreeMap;
import static com.qudini.reactive.utils.MoreCollectors.toUnmodifiableTreeSet;
import static java.util.Map.entry;
import static java.util.function.Function.identity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("MoreCollectors")
class MoreCollectorsTest {

    @Test
    @DisplayName("should allow collecting into a Map with identity values")
    void mapIdentityValues() {
        var map = Stream
                .of("aaa", "c", "bb")
                .collect(toIdentityMap(String::length));
        assertThat(map).containsExactlyInAnyOrderEntriesOf(Map.of(
                3, "aaa",
                1, "c",
                2, "bb"
        ));
        map.put(4, "pass");
    }

    @Test
    @DisplayName("should allow collecting into an unmodifiable Map with identity values")
    void unmodifiableMapIdentityValues() {
        var map = Stream
                .of("aaa", "c", "bb")
                .collect(toUnmodifiableIdentityMap(String::length));
        assertThat(map).containsExactlyInAnyOrderEntriesOf(Map.of(
                3, "aaa",
                1, "c",
                2, "bb"
        ));
        assertThrows(UnsupportedOperationException.class, () -> map.put(4, "fail"));
    }

    @Test
    @DisplayName("should allow collecting into a LinkedHashMap with identity values")
    void linkedMapIdentityValues() {
        var map = Stream
                .of("aaa", "c", "bb")
                .collect(toIdentityLinkedMap(String::length));
        assertThat(map).containsExactly(
                entry(3, "aaa"),
                entry(1, "c"),
                entry(2, "bb")
        );
        map.put(4, "pass");
    }

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
    @DisplayName("should allow collecting into an unmodifiable LinkedHashMap with identity values")
    void unmodifiableLinkedMapIdentityValues() {
        var map = Stream
                .of("aaa", "c", "bb")
                .collect(toUnmodifiableIdentityLinkedMap(String::length));
        assertThat(map).containsExactly(
                entry(3, "aaa"),
                entry(1, "c"),
                entry(2, "bb")
        );
        assertThrows(UnsupportedOperationException.class, () -> map.put(4, "fail"));
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
    @DisplayName("should allow collecting into a TreeMap with identity values")
    void treeMapIdentityValues() {
        var map = Stream
                .of("aaa", "c", "bb")
                .collect(toIdentityTreeMap(String::length));
        assertThat(map).containsExactly(
                entry(1, "c"),
                entry(2, "bb"),
                entry(3, "aaa")
        );
        map.put(4, "pass");
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
    @DisplayName("should allow collecting into an unmodifiable TreeMap with identity values")
    void unmodifiableTreeMapIdentityValues() {
        var map = Stream
                .of("aaa", "c", "bb")
                .collect(toUnmodifiableIdentityTreeMap(String::length));
        assertThat(map).containsExactly(
                entry(1, "c"),
                entry(2, "bb"),
                entry(3, "aaa")
        );
        assertThrows(UnsupportedOperationException.class, () -> map.put(4, "fail"));
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

    @Test
    @DisplayName("should allow grouping into an unmodifiable map")
    void unmodifiableGrouping() {
        var map = Stream
                .of("aaa", "cc", "bb")
                .collect(groupingByUnmodifiable(String::length));
        assertThat(map).containsExactlyInAnyOrderEntriesOf(Map.of(
                3, List.of("aaa"),
                2, List.of("cc", "bb")
        ));
        assertThrows(UnsupportedOperationException.class, () -> map.put(4, List.of("dddd")));
        assertThrows(UnsupportedOperationException.class, () -> map.get(3).add("ddd"));
    }

    @Test
    @DisplayName("should allow grouping into an unmodifiable map with a custom collector")
    void unmodifiableGroupingToTreeSet() {
        var map = Stream
                .of("aaa", "cc", "bb")
                .collect(groupingByUnmodifiable(String::length, toTreeSet()));
        assertThat(map).containsExactlyInAnyOrderEntriesOf(Map.of(
                3, Set.of("aaa"),
                2, Set.of("bb", "cc")
        ));
        assertThrows(UnsupportedOperationException.class, () -> map.put(4, Set.of("dddd")));
        map.get(3).add("ddd");
    }

    @Test
    @DisplayName("should allow grouping into an unmodifiable map with a custom map supplier")
    void unmodifiableGroupingToTreeMap() {
        var map = Stream
                .of("aaa", "cc", "bb")
                .collect(groupingByUnmodifiable(String::length, TreeMap::new));
        assertThat(map).containsExactly(
                entry(2, List.of("cc", "bb")),
                entry(3, List.of("aaa"))
        );
        assertThrows(UnsupportedOperationException.class, () -> map.put(4, List.of("dddd")));
        assertThrows(UnsupportedOperationException.class, () -> map.get(3).add("ddd"));
    }

    @Test
    @DisplayName("should allow grouping into an unmodifiable map with a custom collector and a custom map supplier")
    void unmodifiableGroupingToTreeMapAndTreeSet() {
        var map = Stream
                .of("aaa", "cc", "bb")
                .collect(groupingByUnmodifiable(String::length, TreeMap::new, toTreeSet()));
        assertThat(map).containsExactly(
                entry(2, Set.of("bb", "cc")),
                entry(3, Set.of("aaa"))
        );
        assertThrows(UnsupportedOperationException.class, () -> map.put(4, Set.of("dddd")));
        map.get(3).add("ddd");
    }

    @Test
    @DisplayName("should allow partitioning into an unmodifiable map")
    void unmodifiablePartitioning() {
        var map = Stream
                .of("aaa", "cc", "bb")
                .collect(partitioningByUnmodifiable(s -> s.length() == 2));
        assertThat(map).containsExactlyInAnyOrderEntriesOf(Map.of(
                true, List.of("cc", "bb"),
                false, List.of("aaa")
        ));
        assertThrows(UnsupportedOperationException.class, () -> map.remove(true));
        assertThrows(UnsupportedOperationException.class, () -> map.get(false).add("ddd"));
    }

    @Test
    @DisplayName("should allow partitioning into an unmodifiable map with a custom collector")
    void unmodifiablePartitioningToTreeSet() {
        var map = Stream
                .of("aaa", "cc", "bb")
                .collect(partitioningByUnmodifiable(s -> s.length() == 2, toTreeSet()));
        assertThat(map).containsExactlyInAnyOrderEntriesOf(Map.of(
                true, Set.of("bb", "cc"),
                false, Set.of("aaa")
        ));
        assertThrows(UnsupportedOperationException.class, () -> map.remove(true));
        map.get(false).add("ddd");
    }

}
